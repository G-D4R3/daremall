package dare.daremall.controller.member;

import dare.daremall.controller.order.OrderForm;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.service.ItemService;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShoppingBagController {

    private final MemberService memberService;
    private final ItemService itemService;
    private final BaggedItemRepository baggedItemRepository;

    @PostMapping(value = "/add")
    public String addBag(@AuthenticationPrincipal LoginUserDetails member,
                         @RequestParam(value = "itemId") Long itemId,
                         @RequestParam(value = "count", defaultValue = "1") int count) {

        memberService.addShoppingBag(itemId, member.getUsername(), count);
        return "redirect:/items/detail?itemId="+itemId;
    }

    @PostMapping(value = "/{bagItemId}/delete")
    public String cancel(@AuthenticationPrincipal LoginUserDetails member,
                             @PathVariable("bagItemId") Long bagItemId) {

        memberService.removeShoppingBag(member.getUsername(), bagItemId);

        return "redirect:/shop";
    }


    @PostMapping(value = {"/update"})
    public String update(@AuthenticationPrincipal LoginUserDetails member,
                         @RequestParam("id") Long id,
                         @RequestParam("count") int count) {

        memberService.updateBaggedItemCount(id, count);

        return "redirect:/shop";
    }

    @GetMapping("")
    public String shoppingBag(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        Member findMember = memberService.findUser(member.getUsername());
        List<BaggedItem> shoppingBag = findMember.getShoppingBag();
        OrderForm orderForm = new OrderForm();
        orderForm.setList(shoppingBag.stream().map(b->new BaggedItemDto(b)).collect(Collectors.toList()));

        model.addAttribute("totalPrice", orderForm.getList().stream().mapToInt(i-> {
            if(i.getChecked()==true) {
                return i.getTotalPrice();
            }
            else return 0;
        }).sum());
        model.addAttribute("orderForm", orderForm); // 그냥 shoppingBag list 바로 넣기

        return "/user/shoppingBag";
    }

    @PostMapping(value = "/check")
    public String check(@AuthenticationPrincipal LoginUserDetails member,
                        @RequestParam("id") Long id) {

        memberService.updateBaggedItemCheck(id);

        return "redirect:/shop";
    }

}
