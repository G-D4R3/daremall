package dare.daremall.controller;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.controller.item.ItemListDto;
import dare.daremall.domain.item.ItemSearch;
import dare.daremall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class AdminController {

    private final ItemService itemService;

    @GetMapping(value = "")
    @Secured({"ROLE_ADMIN"})
    public String adminPage(Model model) {
        return "/admin/admin";
    }

    @GetMapping(value = "/item")
    @Secured({"ROLE_ADMIN"})
    public String itemManage(Model model) {

        List<ItemListDto> items = itemService.findItems().stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("addItemForm", new ItemDto());
        model.addAttribute("updateItemForm", new ItemDto());
        model.addAttribute("item", new String());

        return "/admin/item/itemManage";
    }

    @GetMapping(value = "/item/search")
    @Secured({"ROLE_ADMIN"})
    public String itemSearch(Model model, @RequestParam("item") String item) {

        ItemSearch itemSearch = new ItemSearch(item, "all");
        List<ItemListDto> items = itemService.findItems(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("updateItemForm", new ItemDto());
        model.addAttribute("addItemForm", new ItemDto());
        model.addAttribute("item", item);

        return "/admin/item/itemManage";
    }
}
