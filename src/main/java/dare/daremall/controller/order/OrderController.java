package dare.daremall.controller.order;

import dare.daremall.controller.member.BaggedItemDto;
import dare.daremall.controller.member.LoginUserDetails;
import dare.daremall.controller.member.ShoppingBagController;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import dare.daremall.domain.discountPolicy.DiscountPolicy;
import dare.daremall.repository.BaggedItemRepository;
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
    private final BaggedItemRepository baggedItemRepository;
    private final DiscountPolicy discountPolicy;

    @GetMapping(value = "myOrder")
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

    @GetMapping(value = "/new/{orderOption}")
    public String orderForm(@AuthenticationPrincipal LoginUserDetails member,
                            @PathVariable("orderOption") String option,
                            Model model) {
        List<BaggedItemOrderDto> baggedItem = null;
        if(option.equals("all")) {
            baggedItem = baggedItemRepository.findByMember(member.getUsername())
                    .stream().map(bi -> new BaggedItemOrderDto(bi))
                    .collect(Collectors.toList());
        }
        else if(option.equals("select")) {
            baggedItem = baggedItemRepository.findSelected(member.getUsername())
                    .stream().map(bi -> new BaggedItemOrderDto(bi))
                    .collect(Collectors.toList());
        }

        model.addAttribute("items", baggedItem);
        int totalItemPrice = baggedItem.stream().mapToInt(bi->bi.getTotalPrice()).sum();
        int shippingFee = discountPolicy.isDiscountShip(totalItemPrice)==true? 0:2500;
        model.addAttribute("shippingFee", shippingFee);

        model.addAttribute("totalItemPrice", totalItemPrice);
        model.addAttribute("totalPrice", totalItemPrice+shippingFee);
        model.addAttribute("orderForm", new OrderForm());

        return "/user/order/orderForm";
    }

    @PostMapping(value = "/new")
    public String orderFormWithItem(@AuthenticationPrincipal LoginUserDetails member,
                            @RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "count", required = false, defaultValue = "0") int count) {

        memberService.addShoppingBag(itemId, member.getUsername(), count);
        return "redirect:/order/new/all";
    }

}
