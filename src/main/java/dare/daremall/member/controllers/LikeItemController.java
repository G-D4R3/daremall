package dare.daremall.member.controllers;

import dare.daremall.member.dtos.LikeItemListDto;
import dare.daremall.member.dtos.authDtos.LoginUserDetails;
import dare.daremall.member.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/like")
public class LikeItemController {

    private final MemberService memberService;

    @GetMapping(value = "")
    @Secured({"ROLE_USER"})
    public String likes(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        List<LikeItemListDto> items = memberService.getLikes(member.getUsername()).stream().map(li -> new LikeItemListDto(li)).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "user/likeList";

    }

    @PostMapping(value = "/add")
    @Secured({"ROLE_USER"})
    public String addLike(@AuthenticationPrincipal LoginUserDetails member,
                          @RequestParam("itemId") Long itemId){

        if(member==null) return "redirect:/members/login";

        memberService.updateLikeItem(member.getUsername(), itemId);

        return "redirect:/items/detail?itemId="+itemId;
    }

    @PostMapping(value = "/cancel")
    @Secured({"ROLE_USER"})
    public String cancelLike(@AuthenticationPrincipal LoginUserDetails member,
                              @RequestParam("itemId") Long itemId) {

        if(member==null) return "redirect:/members/login";

        memberService.updateLikeItem(member.getUsername(), itemId);

        return "redirect:/like";
    }
}
