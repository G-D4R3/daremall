package dare.daremall.controller.item;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.Member;
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
@RequestMapping(value = "/like")
public class LikeItemController {

    private final MemberService memberService;

    @GetMapping(value = "")
    public String likes(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        Member findMember = memberService.findUser(member.getUsername());
        List<LikeItemDto> items = findMember.getLikes().stream().map(li -> new LikeItemDto(li)).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "/user/likeList";

    }

    @PostMapping(value = "/add")
    public String addLike(@AuthenticationPrincipal LoginUserDetails member,
                          @RequestParam("itemId") Long itemId){

        if(member==null) return "redirect:/members/login";

        memberService.changeLikeItem(member.getUsername(), itemId);

        return "redirect:/items/detail?itemId="+itemId;
    }

    @PostMapping(value = "/cancel")
    public String cancelLike(@AuthenticationPrincipal LoginUserDetails member,
                              @RequestParam("itemId") Long itemId) {

        if(member==null) return "redirect:/members/login";

        memberService.changeLikeItem(member.getUsername(), itemId);

        return "redirect:/like";
    }
}
