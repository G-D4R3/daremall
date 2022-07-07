package dare.daremall.controller.member.mypage;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateDeliveryInfoForm {

    private Long id;

    @NotBlank(message = "별칭을 입력해주세요")
    private String nickname;
    @NotBlank(message = "수령인 이름을 입력해주세요")
    private String name;
    @NotBlank(message = "연락처를 입력해주세요")
    private String phone;

    @NotBlank(message = "우편번호를 입력해주세요")
    private String zipcode;
    @NotBlank(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotBlank(message = "상세 주소를 입력해주세요")
    private String detail;

    @NotNull(message = "배송지 유형을 선택해주세요")
    private Boolean isDefault;
}
