package dare.daremall.controller;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "")
    public String items(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "item/itemList";
    }

    @GetMapping(value = "/albums")
    public String albums(Model model) {
        List<Album> albums = itemService.findAlbums();
        model.addAttribute("albums", albums);
        return "item/albumList";
    }

    @GetMapping(value = "/books")
    public String books(Model model) {
        List<Book> books = itemService.findBooks();
        model.addAttribute("books", books);
        return "item/bookList";
    }

    @GetMapping(value = "/detail")
    public String itemDetails(@RequestParam("itemId") Long itemId, Model model) {
        Item findItem = itemService.findOne(itemId);
        ItemDetailDto item = new ItemDetailDto();
        item.setId(findItem.getId());
        item.setName(findItem.getName());
        item.setPrice(findItem.getPrice());
        item.setStockQuantity(findItem.getStockQuantity());
        item.setImageUrl("/images/"+findItem.getId()+".png");
        model.addAttribute("item", item);
        return "item/detail";
    }


    // 관리자 추가용
    @PostMapping(value = "/new")
    public String addItem(@RequestBody ItemDto itemDto) {
        if(itemDto.getAuthor()!=null && itemDto.getArtist()==null) {
            Book book = new Book();
            book.setName(itemDto.getName());
            book.setPrice(itemDto.getPrice());
            book.setStockQuantity(itemDto.getStockQuantity());
            book.setAuthor(itemDto.getAuthor());
            book.setIsbn(itemDto.getIsbn());
            itemService.saveItem(book);
        }
        else {
            Album album = new Album();
            album.setName(itemDto.getName());
            album.setPrice(itemDto.getPrice());
            album.setStockQuantity(itemDto.getStockQuantity());
            album.setArtist(itemDto.getArtist());
            album.setEtc(itemDto.getEtc());
            itemService.saveItem(album);
        }



        return "item/itemList"; // 임시
    }
}
