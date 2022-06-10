package dare.daremall.controller.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDetailDto {

    private long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String imageUrl;
    private Boolean forSale;
}
