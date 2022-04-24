package dare.daremall.controller;

import dare.daremall.controller.member.BaggedItemDto;
import dare.daremall.controller.member.LoginUserDetails;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.Order;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {

    private final MemberService memberService;

    @PostMapping(value = "new")
    public String order(//@RequestParam("memberId") Long memberId,
                        @RequestParam(value = "itemId") Long itemId,
                        @RequestParam(value = "count", defaultValue = "1") int count) {

        System.out.println("item id : "+itemId+" count : "+count);

        return "redirect:/items";
    }

    @PostMapping(value = "all")
    public String orderAll(@AuthenticationPrincipal LoginUserDetails member) {
        Member findMember = memberService.findUser(member.getUsername());
        for(BaggedItem item : findMember.getShoppingBag()) {
            if(item.getChecked()==true) System.out.println("item : "+item.getItem().getName());
        }
        return "redirect:/shop";
    }

    @PostMapping(value = "/select")
    public String orderSelect(@AuthenticationPrincipal LoginUserDetails member) {
        Member findMember = memberService.findUser(member.getUsername());
        for(BaggedItem item : findMember.getShoppingBag()) {
            if(item.getChecked()==true) System.out.println("item : "+item.getItem().getName());
        }
        return "redirect:/shop";
    }

}
