package dare.daremall.controller.order;

import dare.daremall.domain.BaggedItem;
import lombok.Getter;

@Getter
public class BaggedItemOrderDto {

    private Long itemId;
    private String name;
    private int price;
    private int count;
    private int totalPrice;


    public BaggedItemOrderDto(BaggedItem baggedItem) {
        this.itemId = baggedItem.getItem().getId();
        this.name = baggedItem.getItem().getName();
        this.price = baggedItem.getPrice();
        this.count = baggedItem.getCount();
        this.totalPrice = baggedItem.getTotalPrice();
    }
}
