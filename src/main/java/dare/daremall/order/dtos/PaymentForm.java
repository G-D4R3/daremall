package dare.daremall.order.dtos;

import dare.daremall.member.domains.Member;
import lombok.Data;

@Data
public class PaymentForm {

    private String name;
    private String phone;
    private String zipcode;
    private String street;
    private String detail;
    private String email;

    public PaymentForm(Member member) {
        this.name = member.getName();
        this.phone = member.getPhone();
        this.zipcode = member.getAddress().getZipcode();
        this.street = member.getAddress().getStreet();
        this.detail = member.getAddress().getDetail();
    }
}
