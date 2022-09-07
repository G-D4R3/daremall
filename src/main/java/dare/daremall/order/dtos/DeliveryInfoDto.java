package dare.daremall.order.dtos;

import dare.daremall.member.domains.DeliveryInfo;
import lombok.Data;

@Data
public class DeliveryInfoDto {

    private Long id;
    private String name;
    private String phone;
    private String nickname;
    private String zipcode;
    private String street;
    private String detail;

    public DeliveryInfoDto(DeliveryInfo deliveryInfo) {
        this.id = deliveryInfo.getId();
        this.name = deliveryInfo.getName();
        this.phone = deliveryInfo.getPhone();
        this.nickname = deliveryInfo.getNickname();
        this.zipcode = deliveryInfo.getAddress().getZipcode();
        this.street = deliveryInfo.getAddress().getStreet();
        this.detail = deliveryInfo.getAddress().getDetail();
    }
}
