package dare.daremall.controller.member.mypage;

import dare.daremall.domain.Address;
import dare.daremall.domain.Member;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String name;
    private String loginId;
    private String phone;
    private Address address;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.loginId = member.getLoginId();
        this.phone = member.getPhone();
        this.address = member.getAddress();
    }
}
