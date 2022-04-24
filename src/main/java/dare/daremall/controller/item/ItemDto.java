package dare.daremall.controller.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDto {

    // 세 개 다 not empty
    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;

    private String artist;
    private String etc;

}
