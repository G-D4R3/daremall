package dare.daremall.controller;

import dare.daremall.controller.member.LoginUserDetails;
import dare.daremall.domain.Member;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/shop")
@RequiredArgsConstructor
public class ShoppingBagController {

    MemberService memberService;

    @PostMapping(value = "/add")
    public String addBag(@AuthenticationPrincipal LoginUserDetails member,
                         @RequestParam(value = "itemId") Long itemId,
                         @RequestParam(value = "count", defaultValue = "1") int count) {

        if(!member.isEnabled()) {
            return "redirect:/items";
        }

        Member findMember = memberService.findUser(member.getUsername());
        findMember.getShoppingBag();


        return "redirect:/items/detail?itemId="+itemId;
    }
}
