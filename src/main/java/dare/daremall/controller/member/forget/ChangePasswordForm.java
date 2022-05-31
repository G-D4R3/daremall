package dare.daremall.controller.member.forget;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
public class ChangePasswordForm {

    private String loginId;
    @NotEmpty(message = "새 비밀번호를 입력해주세요")
    private String password;
    @NotEmpty(message = "비밀번호가 일치하지 않습니다")
    private String passwordConfirm;

    public ChangePasswordForm(String loginId) {
        this.loginId = loginId;
    }
}
