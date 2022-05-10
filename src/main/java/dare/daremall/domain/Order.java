package dare.daremall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
public class Order {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인을 order로 설정
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean shippingFee;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public static Order createOrder(Member member, Delivery delivery, List<OrderItem> orderItems, OrderStatus orderStatus) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(orderStatus);
        order.setOrderDate(LocalDateTime.now());
        if(order.getOrderItems().stream().mapToInt(oi->oi.getTotalPrice()).sum() >= 50000) order.setShippingFee(false);
        else order.setShippingFee(true);
        return order;
    }

    public void cancel() {
        if( delivery.getStatus() == DeliveryStatus.COMP ) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL); // JPA가 Entity 수정만으로 바로 db에 업데이트 시켜줘서 밖에서 쿼리를 직접 짜지 않아도 된다.
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }
    }

    public int getTotalPrice() {
        int res = this.orderItems.stream().mapToInt(oi -> oi.getTotalPrice()).sum();
        if(res >= 50000) return res;
        else return res + 2500; // 배송비 추가
    }

    public void delete() {
        if( this.status != OrderStatus.CANCEL ) {
            throw new IllegalStateException("취소된 주문이 아니면 주문 목록에서 삭제할 수 없습니다.");
        }

        for(OrderItem orderItem: this.orderItems) {
            orderItem.cancel();
        }
        this.orderItems.clear();
    }
}
