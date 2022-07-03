package dare.daremall.controller.member.mypage;

import dare.daremall.domain.Member;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class
UpdateMyInfoForm {

    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    private String phone;

    @NotBlank(message = "우편번호를 입력해주세요")
    private String zipcode;
    @NotBlank(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotBlank(message = "상세 주소를 입력해주세요")
    private String detail;

    public UpdateMyInfoForm() {}

    public UpdateMyInfoForm(Member member) {
        this.phone = member.getPhone();
        this.zipcode = member.getAddress().getZipcode();
        this.street = member.getAddress().getStreet();
        this.detail = member.getAddress().getDetail();
    }

}
