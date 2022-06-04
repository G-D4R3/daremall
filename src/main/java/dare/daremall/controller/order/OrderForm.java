package dare.daremall.controller.order;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderForm {

    @NotEmpty(message = "수령인 이름를 입력해주세요")
    private String name;
    @NotEmpty(message = "수령인 연락처를 입력해주세요")
    private String phone;

    @NotEmpty(message = "우편번호를 입력해주세요")
    private String zipcode;
    @NotEmpty(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotEmpty(message = "상세 주소를 입력해주세요")
    private String detail;

    @NotEmpty(message = "결제 방식을 선택해주세요")
    private String payment;

}
