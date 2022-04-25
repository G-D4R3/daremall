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
        this.imageUrl = "/images/"+item.getId()+".png";
    }

    public ItemListDto(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.price = album.getPrice();
        this.stockQuantity = album.getStockQuantity();
        this.imageUrl = "/images/"+album.getId()+".png";
        this.etc = album.getArtist();
    }

    public ItemListDto(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.price = book.getPrice();
        this.stockQuantity = book.getStockQuantity();
        this.imageUrl = "/images/"+book.getId()+".png";
        this.etc = book.getAuthor();
    }
}
