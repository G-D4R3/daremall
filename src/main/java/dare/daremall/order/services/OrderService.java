package dare.daremall.order.services;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import dare.daremall.member.domains.*;
import dare.daremall.order.domains.Order;
import dare.daremall.order.domains.OrderItem;
import dare.daremall.order.domains.OrderStatus;
import dare.daremall.order.domains.PaymentType;
import dare.daremall.order.dtos.OrderForm;
import dare.daremall.order.dtos.UpdateOrderDto;
import dare.daremall.item.domains.ItemStatus;
import dare.daremall.core.exception.NotEnoughStockException;
import dare.daremall.member.repositories.BaggedItemRepository;
import dare.daremall.member.repositories.MemberRepository;
import dare.daremall.member.services.CertificationService;
import dare.daremall.order.repositories.OrderRepository;
import dare.daremall.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final BaggedItemRepository baggedItemRepository;
    private final CertificationService certificationService;
    private final StatisticsService statisticsService;

    public Order findOne(Long orderId) {
        return orderRepository.findOne(orderId);
    }


    /** 사용자 주문 **/
    @Transactional
    public List<Order> findOrders(String loginId){
        verifyOrders(loginId);
        return orderRepository.findByLoginId(loginId);
    }

    @Transactional
    public void verifyOrders(String loginId){
        //List<Order> orders = orderRepository.findByLoginId(loginId);
        List<Order> orders = orderRepository.findKakaoPay(loginId);
        IamportClient iamportClient = new IamportClient("7850918775710695", "4c02feb6adbf7e576849ea0abb51c0c5a4ba50d730aa99ef219d0b459a44a5fff88d3b433a45efc0");
        for(Order order : orders) {
            if(order.getPaymentType().equals(PaymentType.KAKAO)) {
                try{
                    Payment resp = iamportClient.paymentByImpUid(order.getImpUid()).getResponse();
                    if(resp.getStatus().equals("cancelled") && !order.getStatus().equals(OrderStatus.CANCEL)) {
                        order.setStatus(OrderStatus.CANCEL);
                        orderRepository.save(order);
                    }
                    else if(resp.getStatus().equals("paid") && !(order.getStatus().equals(OrderStatus.PAY))) {
                        order.setStatus(OrderStatus.PAY);
                        orderRepository.save(order);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional
    public Long createOrder(String loginId, OrderForm orderForm, OrderStatus orderStatus, String merchantUid, String impUid) {

        if(orderForm.getPayment().equals("KAKAO") && (merchantUid == null || impUid == null)) {
            throw new IllegalStateException("결제가 완료되지 않았습니다.");
        }

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        Delivery delivery = new Delivery();
        delivery.setName(orderForm.getName());
        delivery.setPhone(orderForm.getPhone());
        delivery.setAddress(new Address(orderForm.getZipcode(), orderForm.getStreet(), orderForm.getDetail()));
        delivery.setStatus(DeliveryStatus.NONE);

        List<BaggedItem> baggedItems = baggedItemRepository.findSelected(loginId);

        for(BaggedItem baggedItem : baggedItems) {
            if(baggedItem.getCount() > baggedItem.getItem().getStockQuantity()) {
                throw new NotEnoughStockException("주문 수량을 초과하였습니다.");
            }
            if(baggedItem.getItem().getItemStatus() != ItemStatus.FOR_SALE) {
                baggedItemRepository.remove(baggedItem.getId());
                throw new IllegalStateException("판매 중단된 상품은 주문할 수 없습니다.");
            }
        }

        List<OrderItem> orderItems = baggedItems.stream().map(bi -> {
            if(orderForm.getPayment().equals("KAKAO")) {
                return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount(), true);
            }
            else {
                return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount(), false);
            }
        }).collect(Collectors.toList());

        Order order = Order.createOrder(member, delivery, orderItems, orderStatus, merchantUid, impUid, orderForm.getPayment());

        baggedItemRepository.removeSelected(loginId);

        orderRepository.save(order);
        memberRepository.save(member);
        if(orderForm.getPayment().equals("KAKAO")) {
            statisticsService.updateOrderStatistics(order);
            statisticsService.updateItemStatistics(order);
        }
        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId, String loginId) {
        Order order = orderRepository.findOrder(orderId, loginId).orElseThrow(() -> new NoSuchElementException("일치하는 주문 정보를 찾을 수 없습니다."));
        OrderStatus status = OrderStatus.valueOf(order.getStatus().toString());
        order.cancel();
        //memberRepository.save(order.getMember());
        if(status.equals(OrderStatus.PAY)) {
            statisticsService.updateOrderStatistics(order);
            statisticsService.updateItemStatistics(order);
        }
    }

    @Transactional
    public void deleteOrder(Long orderId, String loginId) {
        Order order = orderRepository.findOrder(orderId, loginId).orElseThrow(() -> new NoSuchElementException("일치하는 주문 정보를 찾을 수 없습니다."));
        if(order.getStatus()==OrderStatus.CANCEL) {
            orderRepository.remove(order);
        }
        else {
            throw new IllegalStateException("취소하지 않은 주문은 삭제할 수 없습니다.");
        }
    }

    /** **/

    /**
     * 관리자 페이지 -/item
     * 상품 삭제시 주문 상품 삭제
     */
    public void deleteOrderItem(Long itemId, String itemName) throws CoolsmsException {
        List<Order> orders = orderRepository.findByItemId(itemId);
        List<String> memberPhoneNumbers = orders.stream().map(o -> o.getMember().getPhone()).collect(Collectors.toList());
        // ORDER에 한해서 orderItem 삭제 => statistics 수정 필요 x
        // statisticsService.deleteOrderItem(orders, itemId);
        if(orders.size()>0) orderRepository.removeOrderItem(itemId);
        // certificationService.itemNotSaleOrderCancel(memberPhoneNumbers, itemName); 주문 상품 주문 취소 문자 보내기
    }


    /**
     * 관리자 페이지 - /item
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByName(String search) {
        return orderRepository.findByName(search);
    }

    @Transactional
    public void update(UpdateOrderDto updateOrderDto) {
        Order findOrder = orderRepository.findOne(updateOrderDto.getId());

        // PAY -> CANCEL
        if(findOrder.getStatus().equals(OrderStatus.PAY)
                && OrderStatus.valueOf(updateOrderDto.getOrderStatus()).equals(OrderStatus.CANCEL)) {
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
            statisticsService.updateOrderStatistics(findOrder);
            statisticsService.updateItemStatistics(findOrder);
            // send message
        }
        // ORDER -> PAY
        else if (findOrder.getStatus().equals(OrderStatus.ORDER)
                && OrderStatus.valueOf(updateOrderDto.getOrderStatus()).equals(OrderStatus.PAY)){
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
            statisticsService.updateOrderStatistics(findOrder);
            statisticsService.updateItemStatistics(findOrder);
        }
        // ORDER -> CANCEL
        else {
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
            // send message
        }
        findOrder.getDelivery().setStatus(DeliveryStatus.valueOf(updateOrderDto.getDeliveryStatus()));
        orderRepository.save(findOrder);
    }

    public Order findOne(Long orderId, String loginId) {
        return orderRepository.findOrder(orderId, loginId).orElseThrow(() -> new NoSuchElementException("일치하는 주문 정보가 없습니다."));// controller에서 exception 처리
    }

    /** **/
}
