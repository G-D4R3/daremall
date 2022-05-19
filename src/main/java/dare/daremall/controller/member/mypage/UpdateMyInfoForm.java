package dare.daremall.controller.member.mypage;

import dare.daremall.domain.Member;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMyInfoForm {

    @NotEmpty
    private String phone;

    @NotEmpty
    private String zipcode;
    @NotEmpty
    private String street;
    @NotEmpty
    private String detail;

    public UpdateMyInfoForm() {}

    public UpdateMyInfoForm(Member member) {
        this.phone = member.getPhone();
        this.zipcode = member.getAddress().getZipcode();
        this.street = member.getAddress().getStreet();
        this.detail = member.getAddress().getDetail();
    }

}
