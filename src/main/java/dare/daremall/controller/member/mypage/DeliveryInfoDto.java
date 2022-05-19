package dare.daremall.controller.member.mypage;

import dare.daremall.domain.DeliveryInfo;
import lombok.Data;

import java.util.Locale;

@Data
public class DeliveryInfoDto {

    private Long id;
    private String nickname;
    private String name;
    private String phone;

    private String address;

    private String zipcode;
    private String street;
    private String detail;

    private Boolean isDefault;

    public DeliveryInfoDto(DeliveryInfo deliveryInfo) {
        this.id = deliveryInfo.getId();
        this.nickname = deliveryInfo.getNickname();
        this.name = deliveryInfo.getName();
        this.phone = deliveryInfo.getPhone();
        this.zipcode = deliveryInfo.getAddress().getZipcode();
        this.street = deliveryInfo.getAddress().getStreet();
        this.detail = deliveryInfo.getAddress().getDetail();
        this.address = String.format("(%s) %s, %s", this.zipcode, this.street, this.detail);
        this.isDefault = deliveryInfo.getIsDefault();
    }
}
