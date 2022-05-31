package dare.daremall.controller.item;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.LikeItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Item;
import dare.daremall.service.ItemService;
import dare.daremall.service.LikeItemService;
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
@RequestMapping(value = "like")
public class LikeItemController {

    private final MemberService memberService;
    private final ItemService itemService;
    private final LikeItemService likeItemService;

    @GetMapping(value = "/")
    public String likes(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        Member findMember = memberService.findUser(member.getUsername());
        List<LikeItem> likeItems = findMember.getLikes();
        List<Item> items = likeItems.stream().map(likeItem -> likeItem.getItem()).collect(Collectors.toList());
        model.addAttribute("items", items);
        return "/user/likeList";

    }

    @PostMapping(value = "/add")
    public String addLike(@AuthenticationPrincipal LoginUserDetails member,
                          @RequestParam("itemId") Long itemId){

        if(member==null) return "redirect:/members/login";

        Member findMember = memberService.findUser(member.getUsername());
        Item findItem = itemService.findOne(itemId);

        LikeItem findLikeItem = likeItemService.findOne(findMember.getId(), itemId);
        if(findLikeItem==null) {
            LikeItem likeItem = new LikeItem();
            likeItem.setItem(findItem);

            findMember.addLikeItem(likeItem);
            likeItemService.save(likeItem);
        }
        else {
            findMember.removeLikeItem(findLikeItem);
            likeItemService.remove(findLikeItem.getId());
        }
        return "redirect:/items/detail?itemId="+itemId;
    }

    @PostMapping(value = "/like/cancel/{itemId}")
    public String cancelLike(@AuthenticationPrincipal LoginUserDetails member,
                              @PathVariable("itemId") Long itemId) {

        if(member==null) return "redirect:/members/login";

        Member findMember = memberService.findUser(member.getUsername());
        LikeItem likeItem = likeItemService.findOne(findMember.getId(), itemId);
        findMember.removeLikeItem(likeItem);
        likeItemService.remove(likeItem.getId());

        return "redirect:/likes";
    }
}
