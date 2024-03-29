package dare.daremall.admin;

import dare.daremall.admin.dtos.AdForm;
import dare.daremall.admin.dtos.MemberDto;
import dare.daremall.admin.dtos.OrderDto;
import dare.daremall.item.dtos.ItemDto;
import dare.daremall.item.dtos.ItemListDto;
import dare.daremall.order.dtos.UpdateOrderDto;
import dare.daremall.ad.domains.Ad;
import dare.daremall.item.domains.Item;
import dare.daremall.item.domains.ItemSearch;
import dare.daremall.statistics.domains.ItemStatistics;
import dare.daremall.statistics.domains.OrderStatistics;
import dare.daremall.ad.AdRepository;
import dare.daremall.item.repositories.ItemRepository;
import dare.daremall.order.repositories.OrderRepository;
import dare.daremall.item.services.ItemService;
import dare.daremall.member.services.MemberService;
import dare.daremall.order.services.OrderService;
import dare.daremall.statistics.StatisticsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class AdminController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final AdRepository adRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final StatisticsService statisticsService;

    @GetMapping(value = "")
    @Secured({"ROLE_ADMIN"})
    public String adminPage(Model model) {
        return "admin/admin";
    }

    @GetMapping(value = "/item")
    @Secured({"ROLE_ADMIN"})
    public String itemManage(Model model, @RequestParam(value = "status", required = false) String status) {

        if(status==null) {
            List<ItemListDto> items = itemService.findItems().stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
            model.addAttribute("items", items);
        }
        else {
            List<ItemListDto> items = itemRepository.findByStatus(status).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());
            model.addAttribute("items", items);
        }
        model.addAttribute("addItemForm", new ItemDto());
        model.addAttribute("updateItemForm", new ItemDto());
        model.addAttribute("item", new String());

        return "admin/item/itemManage";
    }

    @GetMapping(value = "/item/search")
    @Secured({"ROLE_ADMIN"})
    public String itemSearch(Model model, @RequestParam("item") String item) {

        if(item.isEmpty()) return "redirect:/admin/item";

        ItemSearch itemSearch = new ItemSearch(item, "all");
        List<ItemListDto> items = itemService.findItems(itemSearch).stream().map(i -> new ItemListDto(i)).collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("updateItemForm", new ItemDto());
        model.addAttribute("addItemForm", new ItemDto());
        model.addAttribute("item", item);

        return "admin/item/itemManage";
    }

    @GetMapping(value = "/member")
    @PreAuthorize("hasRole('ADMIN')")
    public String memberManage(Model model, @RequestParam(value = "role", required = false) String role) {
        if(role!=null){
            if(role.equals("admin")){
                List<MemberDto> members = memberService.findAdmins().stream().map(m->new MemberDto(m)).collect(Collectors.toList());
                model.addAttribute("members", members);
            }
        }
        else {
            List<MemberDto> members = memberService.findMembers().stream().map(m->new MemberDto(m)).collect(Collectors.toList());
            model.addAttribute("members", members);
        }
        model.addAttribute("memberSearch", new String());
        return "admin/member/memberManage";
    }

    @GetMapping(value = "/member/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String memberSearch(Model model, @RequestParam("memberSearch") String memberSearch) {
        if(memberSearch.isEmpty()) return "redirect:/admin/member";
        List<MemberDto> members = memberService.findMembersByString(memberSearch).stream().map(m->new MemberDto(m)).collect(Collectors.toList());
        model.addAttribute("members", members);
        model.addAttribute("memberSearch", memberSearch);
        return "admin/member/memberManage";
    }

    @GetMapping(value = "/ad")
    @PreAuthorize("hasRole('ADMIN')")
    public String adManage(Model model) {
        List<Ad> ads = adRepository.findAll();
        model.addAttribute("ads", ads);
        model.addAttribute("adSearch", new String());
        model.addAttribute("addAdForm", new AdForm());
        model.addAttribute("updateAdForm", new AdForm());
        return "admin/ad/adManage";
    }

    @GetMapping(value = "/ad/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String adSearch(Model model, @RequestParam("adSearch") String search) {
        if(search.isEmpty()) return "redirect:/admin/ad";
        List<Ad> ads = adRepository.findByName(search);
        model.addAttribute("ads", ads);
        model.addAttribute("adSearch", new String());
        model.addAttribute("addAdForm", new AdForm());
        model.addAttribute("updateAdForm", new AdForm());
        return "admin/ad/adManage";
    }

    @GetMapping(value = "/order")
    @PreAuthorize("hasRole('ADMIN')")
    public String orderManage(Model model, @RequestParam(value = "status", required = false) String status) {
        if(status==null) {
            List<OrderDto> orders = orderService.findAll().stream().map(o->new OrderDto(o)).collect(Collectors.toList());
            model.addAttribute("orders", orders);
        }
        else if(status.equals("now")) {
            List<OrderDto> orders = orderRepository.findNow().stream().map(o->new OrderDto(o)).collect(Collectors.toList());
            model.addAttribute("orders", orders);
        }
        else if(status.equals("comp")) {
            List<OrderDto> orders = orderRepository.findComp().stream().map(o->new OrderDto(o)).collect(Collectors.toList());
            model.addAttribute("orders", orders);
        }
        else if(status.equals("cancel")) {
            List<OrderDto> orders = orderRepository.findCancel().stream().map(o->new OrderDto(o)).collect(Collectors.toList());
            model.addAttribute("orders", orders);

        }
        model.addAttribute("orderSearch", new String());
        model.addAttribute("updateOrderForm", new UpdateOrderDto());
        return "admin/order/orderManage";
    }

    @GetMapping(value = "/order/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String orderSearch(Model model, @RequestParam("orderSearch") String search) {
        if(search.isEmpty()) {
            return "redirect:/admin/order";
        }
        List<OrderDto> orders = orderService.findByName(search).stream().map(o->new OrderDto(o)).collect(Collectors.toList());
        model.addAttribute("orders", orders);
        model.addAttribute("orderSearch", search);
        model.addAttribute("updateOrderForm", new UpdateOrderDto());
        return "admin/order/orderManage";
    }

    @GetMapping(value = "/analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public String analysis(Model model) {
        List<ItemSelectDto> items = itemService.findItems().stream().map(i->new ItemSelectDto(i)).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "admin/analysis/analysis";
    }

    @Getter
    private class ItemSelectDto {
        Long id;
        String name;

        private ItemSelectDto(Item item) {
            this.id = item.getId();
            this.name = item.getName();
        }
    }

    @GetMapping(value = "/analysis/getOrderStatistics")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody List<OrderStatistics> getOrderStatistics() {
        return statisticsService.findAllOrderStatisticsWeek();
    }

    @GetMapping(value = "/analysis/getItemStatistics")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody List<ItemStatistics> getItemStatistics(@RequestParam(value = "itemId") Long itemId) {
        if(itemId==null) {
            return null;
        }
        else return statisticsService.findItemStatistics(itemId);
    }


}

