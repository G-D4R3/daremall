package dare.daremall.controller.order;

import dare.daremall.controller.member.BaggedItemDto;
import dare.daremall.controller.member.LoginUserDetails;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
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
    private final OrderService orderService;

    @GetMapping(value = "/all")
    public String orderAll(@AuthenticationPrincipal LoginUserDetails member) {
        if(member==null) return "redirect:/members/login";
        orderService.orderAll(member.getUsername());
        return "redirect:/order";
    }

    @GetMapping(value = "/select")
    public String orderSelect(@AuthenticationPrincipal LoginUserDetails member) {
        if(member==null) return "redirect:/members/login";
        orderService.orderSelect(member.getUsername());
        return "redirect:/order";
    }

    @GetMapping(value = "")
    public String orderList(@AuthenticationPrincipal LoginUserDetails member,
                            Model model) {

        if(member==null) return "redirect:/members/login";

        List<Order> orders = orderService.findOrders(member.getUsername());
        List<OrderDto> orderDtos = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());

        model.addAttribute("orders", orderDtos);

        return "/user/order/orderList";
    }

    @PostMapping(value = "/{orderId}/cancel")
    public String cancelOrder(@AuthenticationPrincipal LoginUserDetails member,
                              @PathVariable("orderId") Long orderId) {
        if(member==null) return "redirect:/members/login";
        orderService.cancelOrder(orderId);
        return "redirect:/order";
    }

}
