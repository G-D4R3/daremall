package dare.daremall.controller.admin;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.controller.item.ItemListDto;
import dare.daremall.controller.order.UpdateOrderDto;
import dare.daremall.domain.Order;
import dare.daremall.domain.ad.Ad;
import dare.daremall.domain.ad.MainAd;
import dare.daremall.domain.item.ItemSearch;
import dare.daremall.repository.AdRepository;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.OrderRepository;
import dare.daremall.service.ItemService;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final AdRepository adRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping(value = "")
    @Secured({"ROLE_ADMIN"})
    public String adminPage(Model model) {
        return "/admin/admin";
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

        return "/admin/item/itemManage";
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

        return "/admin/item/itemManage";
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
        List<MemberDto> members = memberService.findMembers(memberSearch).stream().map(m->new MemberDto(m)).collect(Collectors.toList());
        model.addAttribute("members", members);
        model.addAttribute("memberSearch", memberSearch);
        return "/admin/member/memberManage";
    }

    @GetMapping(value = "/ad")
    @PreAuthorize("hasRole('ADMIN')")
    public String adManage(Model model) {
        List<Ad> ads = adRepository.findAll();
        model.addAttribute("ads", ads);
        model.addAttribute("adSearch", new String());
        model.addAttribute("addAdForm", new AdForm());
        model.addAttribute("updateAdForm", new AdForm());
        return "/admin/ad/adManage";
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
        return "/admin/ad/adManage";
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
        return "/admin/order/orderManage";
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
        return "/admin/order/orderManage";
    }

    @GetMapping(value = "/analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public String analysis(Model model) {
        return "/admin/analysis/analysis";
    }


}
