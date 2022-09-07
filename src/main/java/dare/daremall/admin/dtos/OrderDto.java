package dare.daremall.admin.dtos;

import dare.daremall.order.domains.Order;
import dare.daremall.order.domains.OrderStatus;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class OrderDto {

    private Long id;

    private String name;
    private String loginId;

    private String orderDate;

    private String title;
    private int price;
    private String status;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.name = order.getMember().getName();
        this.loginId = order.getMember().getLoginId();

        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.title = order.getTitle();
        this.price = order.getTotalPrice();
        if(order.getStatus()== OrderStatus.PAY) {
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
