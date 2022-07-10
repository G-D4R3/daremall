package dare.daremall.controller.member.mypage;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.member.mypage.BaggedItemDto;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.service.ItemService;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
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
    private final BaggedItemRepository baggedItemRepository;

    @GetMapping("") @Secured({"ROLE_USER"})
    public String shoppingBag(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        List<BaggedItemDto> list = baggedItemRepository.findByMember(member.getUsername()).stream().map(b -> new BaggedItemDto(b)).collect(Collectors.toList());

        model.addAttribute("totalPrice", list.stream().mapToInt(i-> {
            if(i.getChecked()==true) {
                return i.getTotalPrice();
            }
            else return 0;
        }).sum());
        model.addAttribute("list", list);

        return "user/shoppingBag";
    }

    @PostMapping(value = "/add") @Secured({"ROLE_USER"})
    public @ResponseBody ResponseEntity<String> addBag(@AuthenticationPrincipal LoginUserDetails member,
                                  @RequestParam(value = "itemId") Long itemId,
                                  @RequestParam(value = "count", defaultValue = "1") int count) {

        if(member==null) throw new AccessDeniedException("로그인이 필요한 서비스입니다.");

        memberService.addShoppingBag(itemId, member.getUsername(), count);
        return new ResponseEntity<>("장바구니에 상품을 추가했습니다.", HttpStatus.OK);
    }


    @PostMapping(value = "/{bagItemId}/delete") @Secured({"ROLE_USER"})
    public String cancel(@AuthenticationPrincipal LoginUserDetails member,
                             @PathVariable("bagItemId") Long bagItemId) {

        if(member==null) return "redirect:/members/login";

        memberService.removeShoppingBag(member.getUsername(), bagItemId);

        return "redirect:/shop";
    }


    @PostMapping(value = {"/update"}) @Secured({"ROLE_USER"})
    public String update(@AuthenticationPrincipal LoginUserDetails member,
                         @RequestParam("id") Long id,
                         @RequestParam("count") int count) {

        if(member==null) return "redirect:/members/login";

        memberService.updateBaggedItemCount(id, count);

        return "redirect:/shop";
    }

    @PostMapping(value = "/check") @Secured({"ROLE_USER"})
    public String check(@AuthenticationPrincipal LoginUserDetails member,
                        @RequestParam("id") Long id) {

        if(member==null) return "redirect:/members/login";

        memberService.updateBaggedItemCheck(id);

        return "redirect:/shop";
    }

}
