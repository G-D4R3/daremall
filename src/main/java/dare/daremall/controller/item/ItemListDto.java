package dare.daremall.controller.item;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import lombok.Data;

@Data
public class ItemListDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String imageUrl;
    private String etc;

    public ItemListDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.stockQuantity = item.getStockQuantity();
        this.imageUrl = item.getImagePath();
        if(item.getClass().equals(Album.class)) {
            this.etc = ((Album) item).getArtist();
        }
        else if (item.getClass().equals(Book.class)){
            this.etc = ((Book) item).getAuthor();
        }
    }
}
