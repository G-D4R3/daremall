package dare.daremall.order.dtos;

import dare.daremall.member.domains.BaggedItem;
import lombok.Getter;

@Getter
public class BaggedItemOrderDto {

    private Long itemId;
    private String name;
    private int price;
    private int count;
    private int totalPrice;
    private String imagePath;


    public BaggedItemOrderDto(BaggedItem baggedItem) {
        this.itemId = baggedItem.getItem().getId();
        this.name = baggedItem.getItem().getName();
        this.price = baggedItem.getPrice();
        this.count = baggedItem.getCount();
        this.totalPrice = baggedItem.getTotalPrice();
        this.imagePath = baggedItem.getItem().getImagePath();
    }
}
