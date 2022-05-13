package dare.daremall.controller.member.forget;

import lombok.Data;

@Data
public class ForgetIdDto {

    private String name;
    private String phone;

    public ForgetIdDto() {
    }

    public ForgetIdDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
