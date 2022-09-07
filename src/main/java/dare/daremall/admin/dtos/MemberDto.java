package dare.daremall.admin.dtos;

import dare.daremall.member.domains.Address;
import dare.daremall.member.domains.Member;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;

    private String name;
    private String loginId;
    private String phone;
    private Address address;
    private String role;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.loginId = member.getLoginId();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.role = member.getRole().toString();
    }
}
