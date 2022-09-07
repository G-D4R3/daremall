package dare.daremall.member.dtos.authDtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginForm {

    @NotBlank(message = "아이디는 필수입니다")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

}
