package dare.daremall.controller.order;

import dare.daremall.domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long itemId;
    private String name;
    private int count;
    private int price;

    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.itemId = orderItem.getItem().getId();
        this.name = orderItem.getItem().getName();
        this.count = orderItem.getCount();
        this.price = orderItem.getTotalPrice();

    }
}
