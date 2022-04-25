package dare.daremall.controller.order;

import dare.daremall.domain.Order;
import dare.daremall.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class OrderDto {

    private Long id;
    private String title;
    private int price;
    private String orderDate;
    private String status;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.title = order.getOrderItems().get(0).getItem().getName();
        if(order.getOrderItems().size()>1) this.title += " 외 " + (order.getOrderItems().size()-1);
        this.price = order.getOrderItems().stream().mapToInt(i->i.getTotalPrice()).sum();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.status = order.getStatus()== OrderStatus.ORDER? "주문완료":"주문취소";
    }
}
