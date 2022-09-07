package dare.daremall.order.dtos;

import dare.daremall.order.domains.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UpdateOrderDto {

    private Long id;
    private String name;
    private String loginId;
    private String orderDate;
    private List<OrderItemDto> orderItems;
    private String orderStatus;
    private String deliveryStatus;
    private int totalPrice;

    public UpdateOrderDto(Order order) {
        this.id = order.getId();
        this.name = order.getMember().getName();
        this.loginId = order.getMember().getLoginId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.orderItems = order.getOrderItems().stream().map(oi -> new OrderItemDto(oi)).collect(Collectors.toList());
        this.orderStatus = order.getStatus().toString();
        this.deliveryStatus = order.getDelivery().getStatus().toString();
        this.totalPrice = order.getTotalPrice();
    }
}
