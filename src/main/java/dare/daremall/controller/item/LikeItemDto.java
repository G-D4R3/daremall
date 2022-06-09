package dare.daremall.controller.item;

import dare.daremall.domain.LikeItem;
import lombok.Data;

@Data
public class LikeItemDto {

    private Long id;
    private String name;
    private int price;

    public LikeItemDto(LikeItem likeItem) {
        this.id = likeItem.getItem().getId();
        this.name = likeItem.getItem().getName();
        this.price = likeItem.getItem().getPrice();
    }
}
