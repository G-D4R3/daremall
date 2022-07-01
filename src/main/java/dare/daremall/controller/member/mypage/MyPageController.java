package dare.daremall.controller.member.mypage;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.order.OrderDto;
import dare.daremall.domain.Member;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.CertificationService;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/userinfo")
public class MyPageController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final CertificationService certificationService;
    private final PasswordEncoder encoder;
    /**
     * 마이페이지
     */

    @GetMapping(value = "") @Secured({"ROLE_USER"})
    public String userInfo(@AuthenticationPrincipal LoginUserDetails member, Model model) {

        if(member==null) return "redirect:/members/login";

        List<OrderDto> orders = orderService.findOrders(member.getUsername()).stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        if(orders.size()>3) orders = orders.subList(0, 3); // 최근 주문 내역은 3개 까지만 표시

        model.addAttribute("orders", orders);
        return "user/userinfo/userinfo";
    }

    @GetMapping(value = "/myInfo") @Secured({"ROLE_USER"})
    public String myInfo(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";


        Member findMember = memberService.findUser(member.getUsername());
        UpdateMyInfoForm updateForm = new UpdateMyInfoForm(findMember);

        model.addAttribute("updateForm", updateForm);
        model.addAttribute("name", findMember.getName());
        model.addAttribute("changePasswordForm", new ChangePasswordForm());

        return "user/userinfo/myInfo";
    }

    @GetMapping(value = "/orderList") @Secured({"ROLE_USER"})
    public String orderList(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";

        List<OrderDto> orderDtos = orderService.findOrders(member.getUsername()).stream().map(o -> new OrderDto(o)).collect(Collectors.toList());

        model.addAttribute("orders", orderDtos);

        return "user/userinfo/orderList";
    }

    @GetMapping(value = "/myAddress") @Secured({"ROLE_USER"})
    public String myAddress(@AuthenticationPrincipal LoginUserDetails member, Model model) {
        if(member==null) return "redirect:/members/login";

        List<DeliveryInfoDto> deliveryInfoDtos = memberRepository.findDeliveryInfos(member.getUsername()).stream().map(di -> new DeliveryInfoDto(di)).collect(Collectors.toList());
        model.addAttribute("deliveries", deliveryInfoDtos);
        model.addAttribute("deliveryInfoForm", new DeliveryInfoForm());
        model.addAttribute("updateDeliveryForm", new UpdateDeliveryInfoForm());

        return "user/userinfo/myAddress";
    }

    @PostMapping(value = "/myAddress/add") @Secured({"ROLE_USER"})
    public @ResponseBody ResponseEntity<String> addDelivery(@AuthenticationPrincipal LoginUserDetails member, @Valid DeliveryInfoForm deliveryInfoForm) {

        if(member==null) throw new AccessDeniedException("로그인이 필요한 서비스입니다.");

        memberService.addDeliveryInfo(member.getUsername(), deliveryInfoForm);

        return new ResponseEntity<>("배송지를 성공적으로 추가했습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/myAddress/delete") @Secured({"ROLE_USER"})
    public @ResponseBody ResponseEntity<String> deleteDelivery(@AuthenticationPrincipal LoginUserDetails member,
                                 Long deliveryId) {
        if(member==null) throw new AccessDeniedException("로그인이 필요한 서비스입니다.");

        memberService.deleteDeliveryInfo(member.getUsername(), deliveryId);

        return new ResponseEntity<>("배송지를 성공적으로 삭제했습니다.", HttpStatus.OK);
    }

    @PostMapping (value = "/myAddress/getDeliveryInfo") @Secured({"ROLE_USER"})
    public @ResponseBody DeliveryInfoDto getDeliveryInfo(@AuthenticationPrincipal LoginUserDetails member,
                                  @RequestParam("deliveryId") Long deliveryId) {
        return new DeliveryInfoDto(memberRepository.findDeliveryinfo(member.getUsername(), deliveryId).orElse(null));
    }

    @PostMapping(value = "/myAddress/update") @Secured({"ROLE_USER"})
    public @ResponseBody ResponseEntity<String> updateDelivery(@AuthenticationPrincipal LoginUserDetails member,
                                 UpdateDeliveryInfoForm updateDeliveryInfoForm) {
        if(member==null) throw new AccessDeniedException("로그인이 필요한 서비스입니다.");

        memberService.updateDeliveryInfo(member.getUsername(), updateDeliveryInfoForm);

        return new ResponseEntity<>("배송지를 성공적으로 수정했습니다.", HttpStatus.OK);
    }


    @PostMapping(value = "/myInfo/update") @Secured({"ROLE_USER"})
    public @ResponseBody ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal LoginUserDetails member,
                                 @Validated UpdateMyInfoForm updateForm, BindingResult result) {

        if(member==null) throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        if(result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for(FieldError error : result.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
            //[출처] springboot 21강 - ResponseEntity와 @Valid와 @BindResult 사용하기|작성자 metacoding
        }

        memberService.updateUserInfo(member.getUsername(), updateForm.getPhone(), updateForm.getZipcode(), updateForm.getStreet(), updateForm.getDetail());
        return new ResponseEntity<String>("회원 정보를 성공적으로 수정했습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/myInfo/getCertificate") @Secured({"ROLE_USER"})
    public @ResponseBody String getCertificateNumberByName(@AuthenticationPrincipal LoginUserDetails member,
                                                           String phone, String newPhone) throws CoolsmsException {

        if(member==null) return null;
        if(!memberService.findUser(member.getUsername()).getPhone().equals(phone)) return null; // 기존 번호 확인
        //return certificationService.PhoneNumberCheck(newPhone);
        return "1234";
    }

    @PostMapping(value = "/myInfo/varifyPw") @Secured({"ROLE_USER"})
    public @ResponseBody Boolean varifyPassword(@AuthenticationPrincipal LoginUserDetails member,
                                               @ModelAttribute @Validated ChangePasswordForm changePasswordForm) {

        if(member==null) return false;

        if(changePasswordForm.getNewPassword().equals(changePasswordForm.getPasswordConfirm())
        && encoder.matches(changePasswordForm.getPassword(), memberService.findUser(member.getUsername()).getPassword()) == true) {
            return true;
        }
        return false;
    }

    @PostMapping(value = "/myInfo/changePassword") @Secured({"ROLE_USER"})
    public @ResponseBody Boolean changePassword(@AuthenticationPrincipal LoginUserDetails member,
                                 @ModelAttribute @Validated ChangePasswordForm changePasswordForm,
                                 BindingResult result) {


        if(member==null) return false;
        if(result.hasErrors() || changePasswordForm.getNewPassword().equals(changePasswordForm.getPasswordConfirm())==false){
            return false;
        }

        memberService.passwordChange(member.getUsername(), changePasswordForm.getNewPassword());
        return true;
    }
}
