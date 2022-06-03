package dare.daremall.controller.member.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginForm {

    @NotBlank(message = "아이디는 필수입니다")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

}
