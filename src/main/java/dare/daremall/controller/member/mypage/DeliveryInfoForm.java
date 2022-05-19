package dare.daremall.controller.member.mypage;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeliveryInfoForm {

    @NotEmpty(message = "별칭을 입력해주세요")
    private String nickname;
    @NotEmpty(message = "수령인 이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "연락처를 입력해주세요")
    private String phone;

    @NotEmpty(message = "우편번호를 입력해주세요")
    private String zipcode;
    @NotEmpty(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotEmpty(message = "상세 주소를 입력해주세요")
    private String detail;

    private Boolean isDefault;
}
