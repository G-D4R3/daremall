package dare.daremall.service;

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

    @Transactional
    public Order orderSelect(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).get();

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        List<BaggedItem> selected = baggedItemRepository.findSelected(loginId);

        List<OrderItem> orderItems = selected.stream().map(bi -> {
            return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount());
        }).collect(Collectors.toList());

        Order order = Order.createOrder(member, delivery, orderItems);

        for(BaggedItem item:selected) member.removeBaggedItem(item);

        orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order orderAll(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).get();

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        List<BaggedItem> baggedItems = baggedItemRepository.findByMember(loginId);

        List<OrderItem> orderItems = baggedItems.stream().map(bi -> {
            return OrderItem.createOrderItem(bi.getItem(), bi.getPrice(), bi.getCount());
        }).collect(Collectors.toList());

        Order order = Order.createOrder(member, delivery, orderItems);

        for(BaggedItem item:baggedItems) member.removeBaggedItem(item);

        orderRepository.save(order);
        return order;
    }

    public List<Order> findOrders(String loginId) {
        return orderRepository.findByLoginId(loginId);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }
}
