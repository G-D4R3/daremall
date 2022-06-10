package dare.daremall.controller.item;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.LikeItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.domain.item.ItemSearch;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "")
    public String items(Model model) {
        List<ItemListDto> items = itemService.findItems().stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "item/itemList";
    }

    @GetMapping(value = "/albums")
    public String albums(Model model) {
        List<ItemListDto> albums = itemService.findAlbums().stream().map(i -> {
            return new ItemListDto(i);
        }).collect(Collectors.toList());
        model.addAttribute("albums", albums);
        return "item/albumList";
    }

    @GetMapping(value = "/books")
    public String books(Model model) {
        List<ItemListDto> books = itemService.findBooks().stream().map(i -> {
            return new ItemListDto(i);
        }).collect(Collectors.toList());
        model.addAttribute("books", books);
        return "item/bookList";
    }

    @GetMapping(value = "/detail")
    public String itemDetailsWithMember(@AuthenticationPrincipal LoginUserDetails member, @RequestParam("itemId") Long itemId, Model model) {
        Item findItem = itemService.findOne(itemId);
        ItemDetailDto item = new ItemDetailDto();
        item.setId(findItem.getId());
        item.setName(findItem.getName());
        item.setPrice(findItem.getPrice());
        item.setStockQuantity(findItem.getStockQuantity());
        item.setImageUrl("/images/"+findItem.getId()+".png");
        model.addAttribute("item", item);

        if(member!=null) {
            Member findMember = memberRepository.findByLoginId(member.getUsername()).get();
            List<LikeItem> likes = findMember.getLikes();
            for(LikeItem likeItem : likes) {
                if(likeItem.getItem().getId()==itemId) {
                    model.addAttribute("isLikeItem", "btn-primary");
                    return "item/detail";
                }
            }
        }
        model.addAttribute("isLikeItem", "btn-outline-primary");
        return "item/detail";
    }

    @GetMapping(value = "/search")
    public String itemSearch(@RequestParam(value = "option") String option,
                             @RequestParam(value = "searchName") String name,
                             Model model) {

        List<ItemListDto> items = null;
        ItemSearch itemSearch = new ItemSearch(name, option);

        if(itemSearch.getOption().equals("all")) {
            items = itemService.findItems(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
        }
        else if(itemSearch.getOption().equals("album")) {
            items = itemService.findAlbums(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
        }
        else if(itemSearch.getOption().equals("book")) {
            items = itemService.findBooks(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
        }

        items.sort(new Comparator<ItemListDto>() {
            @Override
            public int compare(ItemListDto o1, ItemListDto o2) {
                return Long.compare(o1.getId(),o2.getId());
            }
        });
        model.addAttribute("items", items);
        model.addAttribute("option", option);
        model.addAttribute("name", name);

        return "item/searchList";
    }

    /** ADMIN **/

    // 관리자 추가용
    @PostMapping(value = "/new")
    @Secured({"ROLE_ADMIN"})
    public String addItem(@RequestBody ItemDto itemDto) {
        if(itemDto.getType().equals("B")) {
            Book book = new Book();
            book.setName(itemDto.getName());
            book.setPrice(itemDto.getPrice());
            book.setStockQuantity(itemDto.getStockQuantity());
            book.setAuthor(itemDto.getAuthor());
            book.setIsbn(itemDto.getIsbn());
            itemService.saveItem(book);
        }
        else if(itemDto.getType().equals("A")) {
            Album album = new Album();
            album.setName(itemDto.getName());
            album.setPrice(itemDto.getPrice());
            album.setStockQuantity(itemDto.getStockQuantity());
            album.setArtist(itemDto.getArtist());
            album.setEtc(itemDto.getEtc());
            itemService.saveItem(album);
        }
        return "redirect:/admin/item";
    }

    @PostMapping(value = "/findItem")
    public @ResponseBody ItemDto findItem(Long itemId) {
        Item findItem = itemService.findOne(itemId);
        ItemDto item = new ItemDto();
        item.setId(findItem.getId());
        item.setName(findItem.getName());
        item.setPrice(findItem.getPrice());
        item.setStockQuantity(findItem.getStockQuantity());

        if(findItem.getClass().equals(Album.class)) {
            item.setArtist(((Album) findItem).getArtist());
            item.setEtc(((Album) findItem).getEtc());
            item.setType("A");
        }
        else if(findItem.getClass().equals(Book.class)) {
            item.setAuthor(((Book) findItem).getAuthor());
            item.setIsbn(((Book) findItem).getIsbn());
            item.setType("B");
        }

        return item;
    }


}
