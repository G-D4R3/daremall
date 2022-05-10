package dare.daremall.service;

import dare.daremall.controller.order.OrderDto;
import dare.daremall.controller.order.OrderForm;
import dare.daremall.domain.*;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Bag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final BaggedItemRepository baggedItemRepository;

    public List<Order> findOrders(String loginId) {
        return orderRepository.findByLoginId(loginId);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    public Order findOne(Long orderId) {
        return orderRepository.findOne(orderId);
    }

    @Transactional
    public Long createOrder(String loginId, OrderForm orderForm, OrderStatus orderStatus) {
        Member member = memberRepository.findByLoginId(loginId).get();

        Delivery delivery = new Delivery();
        delivery.setName(orderForm.getName());
        delivery.setPhone(orderForm.getPhone());
        delivery.setAddress(new Address(orderForm.getCity(), orderForm.getStreet(), orderForm.getZipcode()));
        delivery.setStatus(DeliveryStatus.NONE);

        List<BaggedItem> baggedItems = baggedItemRepository.findSelected(loginId);

        List<OrderItem> orderItems = baggedItems.stream().map(bi -> {
            return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount());
        }).collect(Collectors.toList());

        Order order = Order.createOrder(member, delivery, orderItems, orderStatus);

        for(BaggedItem item:baggedItems) member.removeBaggedItem(item);

        orderRepository.save(order);
        return order.getId();
    }
}
