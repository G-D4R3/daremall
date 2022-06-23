package dare.daremall.service;

import dare.daremall.controller.admin.OrderDto;
import dare.daremall.controller.order.OrderForm;
import dare.daremall.controller.order.UpdateOrderDto;
import dare.daremall.domain.*;
import dare.daremall.domain.item.ItemStatus;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.hibernate.mapping.Bag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<Order> findOrders(String loginId) {
        return orderRepository.findByLoginId(loginId);
    }

    @Transactional
    public Long createOrder(String loginId, OrderForm orderForm, OrderStatus orderStatus, String merchantUid, String impUid) {
        Member member = memberRepository.findByLoginId(loginId).get();

        Delivery delivery = new Delivery();
        delivery.setName(orderForm.getName());
        delivery.setPhone(orderForm.getPhone());
        delivery.setAddress(new Address(orderForm.getZipcode(), orderForm.getStreet(), orderForm.getDetail()));
        delivery.setStatus(DeliveryStatus.NONE);

        List<BaggedItem> baggedItems = baggedItemRepository.findSelected(loginId);

        for(BaggedItem baggedItem : baggedItems) {
            if(baggedItem.getCount() > baggedItem.getItem().getStockQuantity()) {
                throw new IllegalStateException("주문 수량을 초과하였습니다.");
            }
            if(baggedItem.getItem().getItemStatus() != ItemStatus.FOR_SALE) {
                baggedItemRepository.remove(baggedItem.getId());
                throw new IllegalStateException("판매 중단된 상품은 주문할 수 없습니다.");
            }
        }

        List<OrderItem> orderItems = baggedItems.stream().map(bi -> {
            return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount());
        }).collect(Collectors.toList());

        Order order = Order.createOrder(member, delivery, orderItems, orderStatus, merchantUid, impUid);

        for(BaggedItem item:baggedItems) member.removeBaggedItem(item);

        orderRepository.save(order);
        memberRepository.save(member);
        statisticsService.updateOrderStatistics(order);
        statisticsService.updateItemStatistics(order);
        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
        memberRepository.save(order.getMember());
        statisticsService.updateOrderStatistics(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        if(order.getStatus()==OrderStatus.CANCEL) {
            order.delete();
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
        // 주문 상품 취소된 만큼 order 통계 다시 내기
        statisticsService.deleteOrderItem(orders, itemId);
        orderRepository.removeOrderItem(itemId);
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
        if(!findOrder.getStatus().equals(OrderStatus.valueOf(updateOrderDto.getOrderStatus()))
                && (OrderStatus.valueOf(updateOrderDto.getOrderStatus()).equals(OrderStatus.CANCEL)
                || OrderStatus.valueOf(updateOrderDto.getOrderStatus()).equals(OrderStatus.ORDER))) {
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
            statisticsService.updateOrderStatistics(findOrder);
            statisticsService.updateItemStatistics(findOrder);
        }
        else if (!findOrder.getStatus().equals(OrderStatus.valueOf(updateOrderDto.getOrderStatus()))
                && OrderStatus.valueOf(updateOrderDto.getOrderStatus()).equals(OrderStatus.PAY)){
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
            statisticsService.updateOrderStatistics(findOrder);
            statisticsService.updateItemStatistics(findOrder);
        }
        else {
            findOrder.setStatus(OrderStatus.valueOf(updateOrderDto.getOrderStatus()));
        }
        findOrder.getDelivery().setStatus(DeliveryStatus.valueOf(updateOrderDto.getDeliveryStatus()));
        orderRepository.save(findOrder);
    }

    /** **/
}
