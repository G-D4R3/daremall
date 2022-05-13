package dare.daremall.controller.member.forget;

import lombok.Data;

@Data
public class ChangePasswordForm {

    private String loginId;
    private String password;

    public ChangePasswordForm(String loginId) {
        this.loginId = loginId;
    }
}
