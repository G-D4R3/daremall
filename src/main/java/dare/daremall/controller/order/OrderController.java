package dare.daremall.controller.order;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.*;
import dare.daremall.domain.discountPolicy.DiscountPolicy;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        return "redirect:/order/myOrder";
    }

    @PostMapping(value = "/{orderId}/delete")
    public String deleteOrder(@AuthenticationPrincipal LoginUserDetails member,
                              @PathVariable("orderId") Long orderId) {
        if(member==null) return "redirect:/members/login";
        orderService.deleteOrder(orderId);
        return "redirect:/order/myOrder";
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
            baggedItemRepository.setAllChecked(member.getUsername());
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

    @PostMapping(value = "/createOrder")
    public String createOrder(@AuthenticationPrincipal LoginUserDetails member,
                              @Valid OrderForm orderForm, BindingResult result) {
        if(result.hasErrors()) {
            return "redirect:/shop";
        }

        System.out.println(orderForm.getPayment());

        Long orderId = orderForm.getPayment().equals("pay")? orderService.createOrder(member.getUsername(), orderForm, OrderStatus.PAY)
                :orderService.createOrder(member.getUsername(), orderForm, OrderStatus.ORDER);

        return "redirect:/order/success/"+orderId;
    }

    @GetMapping(value = "/success/{orderId}")
    public String orderSuccess(@AuthenticationPrincipal LoginUserDetails member,
                               @PathVariable("orderId") Long orderId, Model model) {

        OrderDto orderDto = new OrderDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDto);

        return "/user/order/orderSuccess";
    }

    @GetMapping(value = "/detail")
    public String orderDetail(@AuthenticationPrincipal LoginUserDetails member,
                              @RequestParam("orderId") Long orderId, Model model) {

        OrderDetailDto orderDetailDto = new OrderDetailDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDetailDto);

        return "/user/order/orderDetail";
    }

}
