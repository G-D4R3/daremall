package dare.daremall.controller.member.mypage;

import dare.daremall.domain.Member;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMyInfoForm {

    @NotEmpty(message = "휴대폰 번호를 입력해주세요")
    private String phone;

    @NotEmpty(message = "우편번호를 입력해주세요")
    private String zipcode;
    @NotEmpty(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotEmpty(message = "상세 주소를 입력해주세요")
    private String detail;

    public UpdateMyInfoForm() {}

    public UpdateMyInfoForm(Member member) {
        this.phone = member.getPhone();
        this.zipcode = member.getAddress().getZipcode();
        this.street = member.getAddress().getStreet();
        this.detail = member.getAddress().getDetail();
    }

}
