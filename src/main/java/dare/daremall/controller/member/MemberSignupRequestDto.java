package dare.daremall.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MemberSignupRequestDto {

    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "아이디를 입력해주세요")
    private String loginId;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;

    @NotEmpty(message = "휴대폰 번호를 입력해주세요")
    private String phone;

    private String city;
    private String street;
    private String zipcode;
}
