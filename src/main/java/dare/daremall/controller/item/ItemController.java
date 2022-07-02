package dare.daremall.controller.item;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.LikeItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.*;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.ItemService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final OrderService orderService;

    /*@GetMapping(value = "")
    public String items(Model model) {
        List<ItemListDto> items = itemService.findItemsExceptHide().stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "item/itemList";
    }*/

    @GetMapping(value = "")
    public String itemList(@RequestParam(value = "page") Optional<Integer> page,
                           @RequestParam(value = "option", required = false) String option,
                           @RequestParam(value = "searchName", required = false) String name,
                           Model model) {
        int mPage =  page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);
        Page<?> items = null;
        Page<ItemListDto> dtoPage = null;
        Pager pager = null;

        if(option == null || option.equals("all")) {
            items = itemService.findAllPageable(name==null? "%":"%"+name+"%", PageRequest.of(mPage, 10));
            dtoPage = items.map(item -> new ItemListDto((Item) item));
            pager = new Pager(items.getTotalPages(), items.getNumber(), 5);
        }
        else if(option.equals("album")) {
            items = itemService.findAlbumPageable(name==null? "%":"%"+name+"%", PageRequest.of(mPage, 10));
            dtoPage = items.map(item -> new ItemListDto((Album) item));
            pager = new Pager(items.getTotalPages(), items.getNumber(), 5);
        }
        else if(option.equals("book")) {
            items = itemService.findBookPageable(name==null? "%":"%"+name+"%", PageRequest.of(mPage, 10));
            dtoPage = items.map(item -> new ItemListDto((Book) item));
            pager = new Pager(items.getTotalPages(), items.getNumber(), 5);
        }

        model.addAttribute("items", dtoPage);
        model.addAttribute("selectedPageSize", 5);
        model.addAttribute("pager", pager);
        model.addAttribute("option", option==null? "all":option);
        model.addAttribute("name", name);

        return "item/itemList";
    }

    @GetMapping(value = "/albums")
    public String albumList(@RequestParam(value = "page") Optional<Integer> page, Model model) {
        int mPage =  page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);

        Page<Album> items = itemService.findAlbumPageable("%", PageRequest.of(mPage, 10));
        Page<ItemListDto> dtoPage = items.map(item -> new ItemListDto(item));
        Pager pager = new Pager(items.getTotalPages(), items.getNumber(), 5);

        model.addAttribute("albums", dtoPage);
        model.addAttribute("selectedPageSize", 5);
        model.addAttribute("pager", pager);

        return "item/albumList";
    }

    @GetMapping(value = "/books")
    public String bookList(@RequestParam(value = "page") Optional<Integer> page, Model model) {
        int mPage =  page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);

        Page<Book> items = itemService.findBookPageable("%", PageRequest.of(mPage, 10));
        Page<ItemListDto> dtoPage = items.map(item -> new ItemListDto(item));
        Pager pager = new Pager(items.getTotalPages(), items.getNumber(), 5);

        model.addAttribute("books", dtoPage);
        model.addAttribute("selectedPageSize", 5);
        model.addAttribute("pager", pager);

        return "item/bookList";
    }
    @GetMapping(value = "/detail")
    public String itemDetailsWithMember(@AuthenticationPrincipal LoginUserDetails member, @RequestParam("itemId") Long itemId, Model model) {
        Item findItem = itemService.findOne(itemId);

        if(findItem.getItemStatus()== ItemStatus.HIDE) {
            return "item/hide";
        }

        ItemDetailDto item = new ItemDetailDto();
        item.setId(findItem.getId());
        item.setName(findItem.getName());
        item.setPrice(findItem.getPrice());
        item.setStockQuantity(findItem.getStockQuantity());
        item.setImageUrl(findItem.getImagePath());
        item.setItemStatus(findItem.getItemStatus().toString());
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

    /** ADMIN **/

    // 관리자 추가용
    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<String> addItem(@Validated ItemDto itemDto, MultipartFile imgFile) throws Exception{

        if(!imgFile.isEmpty()) {
            String oriImgName = imgFile.getOriginalFilename();
            String imgName = "";

            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/images/item/thumb/";
            String savedFileName = oriImgName;
            imgName = savedFileName;
            File saveFile = new File(projectPath, imgName);
            imgFile.transferTo(saveFile);

            itemDto.setImagePath("/images/item/thumb/" + imgName);
        }
        else {
            itemDto.setImagePath("/images/default.png");
        }

        itemService.saveItem(itemDto);

        return new ResponseEntity<>("상품을 성공적으로 추가했습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/findItem")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ItemDto findItem(Long itemId) {
        Item findItem = itemService.findOne(itemId);
        ItemDto item = new ItemDto();
        item.setId(findItem.getId());
        item.setName(findItem.getName());
        item.setPrice(findItem.getPrice());
        item.setStockQuantity(findItem.getStockQuantity());
        item.setImagePath(findItem.getImagePath());
        item.setItemStatus(findItem.getItemStatus().toString());

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

    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<String> updateItem (@Validated ItemDto itemDto, MultipartFile imgFile) throws Exception{

        if(!imgFile.isEmpty()) {
            String oriImgName = imgFile.getOriginalFilename();
            String imgName = "";

            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/images/item/thumb/";

            // UUID 를 이용하여 파일명 새로 생성
            // UUID - 서로 다른 객체들을 구별하기 위한 클래스
            //UUID uuid = UUID.randomUUID();
            //String savedFileName = uuid + "_" + oriImgName; // 파일명 -> imgName
            String savedFileName = oriImgName;
            imgName = savedFileName;
            File saveFile = new File(projectPath, imgName);
            imgFile.transferTo(saveFile);

            itemDto.setImagePath("/images/item/thumb/" + imgName);
        }

        itemService.updateItem(itemDto);

        if(ItemStatus.valueOf(itemDto.getItemStatus())!=ItemStatus.FOR_SALE) {
            orderService.deleteOrderItem(itemDto.getId(), itemDto.getName());
        }

        return new ResponseEntity<>("상품을 성공적으로 수정했습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<String> deleteItem(Long itemId) {
        itemService.delete(itemId);
        return new ResponseEntity<>("상품을 성공적으로 삭제했습니다.", HttpStatus.OK);
    }


}
