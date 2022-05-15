package dare.daremall.controller.order;

import dare.daremall.domain.*;
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
    private Delivery delivery;
    private String status;
    private int totalPrice;

    public OrderDetailDto(Order order) {
        this.id = order.getId();
        this.price = order.getOrderItems().stream().mapToInt(i->i.getTotalPrice()).sum();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.orderItems = order.getOrderItems().stream().map(oi -> new OrderItemDto(oi)).collect(Collectors.toList());
        this.delivery = order.getDelivery();
        if(order.getStatus()==OrderStatus.PAY) {
            switch (delivery.getStatus()) {
                case NONE:
                    this.status = "결제 완료";
                    break;
                case READY:
                    this.status = "배송 준비 중";
                    break;
                case SHIP:
                    this.status = "배송 중";
                    break;
                case COMP:
                    this.status = "배송 완료";
                    break;
            }
        }
        else {
            this.status = order.getStatus()==OrderStatus.ORDER? "주문 완료":"주문 취소";
        }

        this.totalPrice = order.getTotalPrice();
    }

}
