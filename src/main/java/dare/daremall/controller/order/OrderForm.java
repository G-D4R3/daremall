package dare.daremall.controller.order;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderForm {

    @NotEmpty(message = "수령인 이름를 입력해주세요")
    private String name;
    @NotEmpty(message = "수령인 연락처를 입력해주세요")
    private String phone;

    @NotEmpty(message = "주소를 입력해주세요")
    private String city;
    @NotEmpty(message = "주소를 입력해주세요")
    private String street;
    @NotEmpty(message = "주소를 입력해주세요")
    private String zipcode;

    private String payment;

}
