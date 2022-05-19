package dare.daremall.controller.member.mypage;


import lombok.Data;

@Data
public class UpdateDeliveryInfoForm {

    private Long id;

    private String name;
    private String phone;
    private String nickname;

    private String zipcode;
    private String street;
    private String detail;

    private Boolean isDefault;
}
