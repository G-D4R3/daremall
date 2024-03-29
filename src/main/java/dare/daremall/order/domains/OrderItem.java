package dare.daremall.order.domains;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dare.daremall.item.domains.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    // factory method
    public static OrderItem createOrderItem(Item item, int orderPrice, int count, boolean isPay) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        if(isPay) item.removeStock(count);

        return orderItem;
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

    public void cancel() {
        getItem().addStock(count); // 재고수량 원복
    }
}
