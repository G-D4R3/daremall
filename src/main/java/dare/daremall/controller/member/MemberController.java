package dare.daremall.controller.member;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.forget.ChangePasswordForm;
import dare.daremall.controller.order.DeliveryInfoDto;
import dare.daremall.domain.Member;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.CertificationService;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CertificationService certificationService;


    @RequestMapping(value = "login")
    public String login() {
        return "user/loginForm";
    }

    /*@GetMapping(value = "/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/loginForm";
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@ModelAttribute(value = "loginForm") @Validated LoginForm form, BindingResult bindingResult) {
        return "login";
    }*/

    @GetMapping(value = "/new")
    public String createMemberForm(Model model) {
        model.addAttribute("memberDto", new MemberSignupRequestDto());
        return "user/createMemberForm";
    }

    @PostMapping(value = "/new")
    public String createMember(@Validated @ModelAttribute("memberDto") MemberSignupRequestDto memberDto, BindingResult result) {
        if(result.hasErrors()) {
            return "user/createMemberForm";
        }
        memberService.join(memberDto);
        return "redirect:/";
    }

    @GetMapping(value = "/new/getCertificate")
    public @ResponseBody String createMemberCertificate(@RequestParam(value = "phone") String phone,
                                                           RedirectAttributes redirectAttributes) throws CoolsmsException {

        redirectAttributes.addAttribute("phone", phone);

        return certificationService.PhoneNumberCheck(phone);
        //return "1234";
    }

    @GetMapping(value = "/new/loginIdValidation")
    public @ResponseBody Boolean loginIdValidation(@RequestParam(value = "loginId") String loginId) {
        if(memberService.findUser(loginId)==null) return true;
        else return false;
    }

    /**
     *
     * 아이디 / 비밀번호 찾기
     */

    @GetMapping(value = "/forgetId")
    public String forgetId(Model model) {
        return "/user/forget/forgetId";
    }

    @GetMapping(value = "/forgetId/getCertificate")
    public @ResponseBody String getCertificateNumberByName(@RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "phone", required = false) String phone,
                                                           RedirectAttributes redirectAttributes) throws CoolsmsException {
        if(memberService.findLoginIdByName(name, phone)==null) return null;
        redirectAttributes.addAttribute("phone", phone);

        return certificationService.PhoneNumberCheck(phone);
        //return "1234";
    }

    @GetMapping(value = "/forgetId/findId")
    public @ResponseBody List<String> findId(@RequestParam("name") String name, @RequestParam("phone") String phone) {
        return memberService.findLoginIdByName(name, phone);
    }

    @GetMapping(value = "/forgetId/success")
    public String findIdSuccess(Model model, @RequestParam("name") String name, @RequestParam("phone") String phone) {
        List<String> loginId = memberService.findLoginIdByName(name, phone);
        model.addAttribute("loginId", loginId);
        return "/user/forget/findId";
    }


    @GetMapping(value = "/forgetPassword")
    public String forgetPassword(Model model) {
        return "/user/forget/forgetPassword";
    }

    @GetMapping(value = "/forgetPassword/getCertificate")
    public @ResponseBody String getCertificateNumberByLoginId(@RequestParam(value = "loginId", required = false) String loginId,
                                                              @RequestParam(value = "phone", required = false) String phone,
                                                              RedirectAttributes redirectAttributes) throws CoolsmsException {
        if(memberService.findMemberByLoginId(loginId, phone)==null) return null;
        redirectAttributes.addAttribute("phone", phone);

        return certificationService.PhoneNumberCheck(phone);
        //return "1234";
    }

    @GetMapping(value = "/forgetPassword/changePassword")
    public String changePasswordForm(Model model,@RequestParam("loginId") String loginId) {

        Member member = memberService.findUser(loginId);
        if(member==null) return "redirect:/members/forgetPassword";

        model.addAttribute("changePasswordForm", new ChangePasswordForm(loginId));

        return "/user/forget/changePassword";
    }

    @PostMapping(value = "/forgetPassword/changePassword")
    public String changePassword(@Validated ChangePasswordForm form, BindingResult result, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            return "/user/forget/chagnePassword";
        }
        if(!form.getNewPassword().equals(form.getPasswordConfirm())) {
            return "/user/forget/chagnePassword";
        }

        memberService.passwordChange(form.getLoginId(), form.getNewPassword());
        return "redirect:/members/forgetPassword/success";
    }

    @GetMapping(value = "/forgetPassword/success")
    public String findPasswordSuccess(Model model) {
        return "/user/forget/findPassword";
    }


    @GetMapping(value = "/findDeliveryInfo")
    public @ResponseBody DeliveryInfoDto findDeliveryInfo(@AuthenticationPrincipal LoginUserDetails member,
                                                          @RequestParam("delivery_id") Long deliveryId) {

        return new DeliveryInfoDto(memberRepository.findDeliveryinfo(member.getUsername(), deliveryId).orElse(null));
    }

}
