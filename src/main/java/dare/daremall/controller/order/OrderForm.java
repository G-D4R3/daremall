package dare.daremall.controller.order;

import lombok.Data;

@Data
public class OrderForm {

    private String name;
    private String phone;

    private String city;
    private String street;
    private String zipcode;

}
