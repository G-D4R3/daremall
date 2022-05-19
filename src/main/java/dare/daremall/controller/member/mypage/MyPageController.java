package dare.daremall.controller.member.mypage;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.order.OrderDto;
import dare.daremall.domain.Member;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/userinfo")
public class MyPageController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
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

        List<DeliveryInfoDto> deliveryInfoDtos = memberService.findUser(member.getUsername()).getDeliveryInfos().stream().map(di -> new DeliveryInfoDto(di)).collect(Collectors.toList());
        model.addAttribute("deliveries", deliveryInfoDtos);
        model.addAttribute("deliveryInfoForm", new DeliveryInfoForm());
        model.addAttribute("updateDeliveryForm", new UpdateDeliveryInfoForm());

        return "/user/userinfo/myAddress";
    }

    @PostMapping(value = "/addDelivery")
    public String addDelivery(@AuthenticationPrincipal LoginUserDetails member, @Valid DeliveryInfoForm deliveryInfoForm) {

        if(member==null) return "redirect:/members/login";

        memberService.addDeliveryInfo(member.getUsername(), deliveryInfoForm);

        return "redirect:/userinfo/myAddress";
    }

    @PostMapping(value = "/myAddress/delete/{deliveryId}")
    public String deleteDelivery(@AuthenticationPrincipal LoginUserDetails member,
                                 @PathVariable("deliveryId") Long deliveryId) {
        if(member==null) return "redirect:/members/login";

        memberService.deleteDeliveryInfo(member.getUsername(), deliveryId);

        return "redirect:/userinfo/myAddress";
    }

    @GetMapping (value = "/myAddress/getDeliveryInfo")
    public @ResponseBody DeliveryInfoDto getDeliveryInfo(@AuthenticationPrincipal LoginUserDetails member,
                                  @RequestParam("deliveryId") Long deliveryId) {
        DeliveryInfoDto deliveryInfoDto = new DeliveryInfoDto(memberRepository.findDeliveryinfo(member.getUsername(), deliveryId).orElse(null));
        return deliveryInfoDto;
    }

    @PostMapping(value = "/myAddress/updateDeliveryInfo")
    public String deleteDelivery(@AuthenticationPrincipal LoginUserDetails member,
                                 UpdateDeliveryInfoForm updateDeliveryInfoForm) {
        if(member==null) return "redirect:/members/login";

        memberService.updateDeliveryInfo(member.getUsername(), updateDeliveryInfoForm);

        return "redirect:/userinfo/myAddress";
    }


    @GetMapping(value = "/myInfo")
    public String myInfo(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";


        Member findMember = memberService.findUser(member.getUsername());
        UpdateMyInfoForm updateForm = new UpdateMyInfoForm(findMember);

        model.addAttribute("updateForm", updateForm);
        model.addAttribute("name", findMember.getName());

        return "/user/userinfo/myInfo";
    }

    @PostMapping(value = "/updateUserInfo")
    public String updateUserInfo(@AuthenticationPrincipal LoginUserDetails member,
                                 @Valid UpdateMyInfoForm updateForm) {

        if(member==null) return "redirect:/members/login";

        System.out.println("hello");

        memberService.updateUserInfo(member.getUsername(), updateForm.getPhone(), updateForm.getZipcode(), updateForm.getStreet(), updateForm.getDetail());
        return "redirect:/userinfo/myInfo";
    }

    @GetMapping(value = "/getCertificateNumber")
    public @ResponseBody String getCertificateNumberByName(@AuthenticationPrincipal LoginUserDetails member,
                                                           @RequestParam(value = "phone") String phone) throws CoolsmsException {

        if(member==null) return null;
        //return certificationService.PhoneNumberCheck(phone);
        return "1234";
    }
}
