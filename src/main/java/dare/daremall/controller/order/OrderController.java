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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping(value = "/cancel")
    public String cancelOrder(@AuthenticationPrincipal LoginUserDetails member,
                              Long orderId) {
        if(member==null) return "redirect:/members/login";
        orderService.cancelOrder(orderId);
        return "redirect:/userinfo/orderList";
    }

    @PostMapping(value = "/delete")
    public String deleteOrder(@AuthenticationPrincipal LoginUserDetails member,
                              Long orderId) {
        if(member==null) return "redirect:/members/login";
        orderService.deleteOrder(orderId);
        return "redirect:/userinfo/orderList";
    }

    @GetMapping(value = "/new/{orderOption}")
    public String orderForm(@AuthenticationPrincipal LoginUserDetails member,
                            @PathVariable("orderOption") String option,
                            Model model) {

        if(member==null) return "redirect:/members/login";

        if(option.equals("all")) {
            memberService.selectAllBagItem(member.getUsername());
        }

        List<BaggedItemOrderDto> baggedItem = baggedItemRepository.findSelected(member.getUsername())
                .stream().map(bi -> new BaggedItemOrderDto(bi))
                .collect(Collectors.toList());

        if(baggedItem.size()==0) {
            return "redirect:/shop";
        }

        model.addAttribute("items", baggedItem);
        int totalItemPrice = baggedItem.stream().mapToInt(bi->bi.getTotalPrice()).sum();
        int shippingFee = discountPolicy.isDiscountShip(totalItemPrice)==true? 0:2500;
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("myDeliveries", memberService.findUser(member.getUsername()).getDeliveryInfos().stream().map(di -> new DeliveryInfoDto(di)).collect(Collectors.toList()));

        model.addAttribute("totalItemPrice", totalItemPrice);
        model.addAttribute("totalPrice", totalItemPrice+shippingFee);
        model.addAttribute("orderForm", new OrderForm());

        return "/user/order/orderForm";
    }

    @PostMapping(value = "/new/addItem")
    public String orderFormWithItem(@AuthenticationPrincipal LoginUserDetails member,
                            @RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "count", required = false, defaultValue = "0") int count) {
        if(member==null) return "redirect:/members/login";

        memberService.addShoppingBag(itemId, member.getUsername(), count);
        return "redirect:/order/new/all";
    }

    @PostMapping(value = "/createOrder")
    public String createOrder(@AuthenticationPrincipal LoginUserDetails member,
                              @Validated OrderForm orderForm, BindingResult result) {

        if(member==null) return "redirect:/members/login";
        if(result.hasErrors()){
            return "redirect:/order/new/select";
        }

        Long orderId = orderForm.getPayment().equals("pay")? orderService.createOrder(member.getUsername(), orderForm, OrderStatus.PAY)
                :orderService.createOrder(member.getUsername(), orderForm, OrderStatus.ORDER);

        return "redirect:/order/success/"+orderId;
    }

    @GetMapping(value = "/success/{orderId}")
    public String orderSuccess(@AuthenticationPrincipal LoginUserDetails member,
                               @PathVariable("orderId") Long orderId, Model model) {

        if(member==null) return "redirect:/members/login";

        OrderDto orderDto = new OrderDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDto);

        return "/user/order/orderSuccess";
    }

    @GetMapping(value = "/detail")
    public String orderDetail(@AuthenticationPrincipal LoginUserDetails member,
                              @RequestParam("orderId") Long orderId, Model model) {

        if(member==null) return "redirect:/members/login";

        OrderDetailDto orderDetailDto = new OrderDetailDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDetailDto);

        return "/user/order/orderDetail";
    }

}
