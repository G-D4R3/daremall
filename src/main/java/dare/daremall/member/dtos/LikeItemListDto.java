package dare.daremall.member.dtos;

import dare.daremall.member.domains.LikeItem;
import lombok.Data;

@Data
public class LikeItemListDto {

    private Long id;
    private String name;
    private int price;

    public LikeItemListDto(LikeItem likeItem) {
        this.id = likeItem.getItem().getId();
        this.name = likeItem.getItem().getName();
        this.price = likeItem.getItem().getPrice();
    }
}
