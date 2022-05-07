package dare.daremall.controller.order;

import dare.daremall.domain.Delivery;
import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import dare.daremall.domain.OrderStatus;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDetailDto {

    private Long id;
    private int price;
    private String orderDate;
    private List<OrderItemDto> orderItems;
    private String status;
    private Delivery delivery;

    public OrderDetailDto(Order order) {
        this.id = order.getId();
        this.price = order.getOrderItems().stream().mapToInt(i->i.getTotalPrice()).sum();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.orderItems = order.getOrderItems().stream().map(oi -> new OrderItemDto(oi)).collect(Collectors.toList());
        this.status = order.getStatus()== OrderStatus.ORDER? "주문완료":"주문취소";
        this.delivery = order.getDelivery();
    }

}
