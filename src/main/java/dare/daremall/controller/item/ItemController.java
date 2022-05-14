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
        List<ItemListDto> items = itemService.findAlbums().stream().map(i -> {
            return new ItemListDto(i);
        }).collect(Collectors.toList());
        items.addAll(itemService.findBooks().stream().map(i -> {
            return new ItemListDto(i);
        }).collect(Collectors.toList()));
        items.sort(new Comparator<ItemListDto>() {
            @Override
            public int compare(ItemListDto o1, ItemListDto o2) {
                return Long.compare(o1.getId(),o2.getId());
            }
        });
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

    @GetMapping(value = "/search")
    public String itemSearch(@RequestParam(value = "option") String option,
                             @RequestParam(value = "name") String name,
                             Model model) {

        List<ItemListDto> items = null;
        ItemSearch itemSearch = new ItemSearch(name, option);

        if(itemSearch.getOption().equals("all")) {
            items = itemService.findAlbums(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
            items.addAll(itemService.findBooks(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList()));
            items.sort(new Comparator<ItemListDto>() {
                @Override
                public int compare(ItemListDto o1, ItemListDto o2) {
                    return Long.compare(o1.getId(),o2.getId());
                }
            });
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
}
