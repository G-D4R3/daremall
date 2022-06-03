package dare.daremall.controller.member.mypage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class ChangePasswordForm {

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]{6,20}$", message = "6-20자 사이의 영문, 숫자로만 입력할 수 있습니다")
    private String newPassword;
    @NotBlank(message = "비밀번호를 확인해주세요")
    private String passwordConfirm;

}
