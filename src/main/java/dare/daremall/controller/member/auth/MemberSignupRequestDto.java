package dare.daremall.controller.member.auth;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class MemberSignupRequestDto {

    @NotBlank(message = "이름을 입력해주세요")
    @Length(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]*$", message = "2-10자 사이의 한글만 입력할 수 있습니다")
    private String name;

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]{6,20}$", message = "6-20자 사이의 영문, 숫자로만 입력할 수 있습니다")
    private String loginId;
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]{6,20}$", message = "6-20자 사이의 영문, 숫자로만 입력할 수 있습니다")
    private String password;

    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "숫자만 입력할 수 있습니다")
    private String phone;

    @NotBlank(message = "우편 번호를 입력해주세요")
    private String zipcode;
    @NotBlank(message = "도로명 주소를 입력해주세요")
    private String street;
    @NotBlank(message = "상세 주소를 입력해주세요")
    private String detail;
}
