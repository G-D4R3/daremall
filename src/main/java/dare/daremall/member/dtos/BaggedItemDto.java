package dare.daremall.member.dtos;

import dare.daremall.member.domains.BaggedItem;
import lombok.Data;

@Data
public class BaggedItemDto {

    private Long id;
    private Long itemId;
    private String name;
    private int price;
    private int totalPrice;
    private int count;
    private int stockQuantity;
    private Boolean checked;

    public BaggedItemDto(BaggedItem item) {
        this.id = item.getId();
        this.itemId = item.getItem().getId();
        this.name = item.getItem().getName();
        this.price = item.getItem().getPrice();
        this.totalPrice = item.getTotalPrice();
        this.count = item.getCount();
        this.stockQuantity = item.getItem().getStockQuantity();
        this.checked = item.getChecked();
    }
}
