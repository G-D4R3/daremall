package dare.daremall.controller.member.mypage;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.order.OrderDto;
import dare.daremall.domain.Member;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/userinfo")
public class MyPageController {

    private final OrderService orderService;
    private final MemberService memberService;
    /**
     * 마이페이지
     */

    @GetMapping(value = "")
    public String userInfo(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        List<OrderDto> orders = orderService.findOrders(member.getUsername()).stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        if(orders.size()>3) orders = orders.subList(0, 3); // 최근 주문 내역은 3개 까지만 표시

        model.addAttribute("orders", orders);
        return "/user/userinfo/userinfo";
    }

    @GetMapping(value = "/orderList")
    public String orderList(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";

        List<OrderDto> orderDtos = orderService.findOrders(member.getUsername()).stream().map(o -> new OrderDto(o)).collect(Collectors.toList());

        model.addAttribute("orders", orderDtos);

        return "/user/userinfo/orderList";
    }

    @GetMapping(value = "/myAddress")
    public String myAddress(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";

        return "/user/userinfo/myAddress";
    }

    @GetMapping(value = "/myInfo")
    public String myInfo(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";

        MemberDto memberDto  = new MemberDto(memberService.findUser(member.getUsername()));
        model.addAttribute("memberDto", memberDto);

        return "/user/userinfo/myInfo";
    }
}
