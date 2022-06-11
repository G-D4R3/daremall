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
    private String imgUrl;
    private String status;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.title = order.getTitle();
        this.price = order.getOrderItems().stream().mapToInt(i->i.getTotalPrice()).sum();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.imgUrl = "/images/"+order.getOrderItems().get(0).getItem().getId()+".png";
        if(order.getStatus()==OrderStatus.PAY) {
            switch (order.getDelivery().getStatus()) {
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
    }
}
