package dare.daremall.controller.member;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.forget.ChangePasswordForm;
import dare.daremall.controller.order.DeliveryInfoDto;
import dare.daremall.domain.Member;
import dare.daremall.domain.MemberRole;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.CertificationService;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

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

    @PostMapping(value = "/new/getCertificate")
    public @ResponseBody String createMemberCertificate(String phone,
                                                        RedirectAttributes redirectAttributes) throws CoolsmsException {

        redirectAttributes.addAttribute("phone", phone);

        //return certificationService.PhoneNumberCheck(phone);
        return "1234";
    }

    @PostMapping(value = "/new/loginIdValidation")
    public @ResponseBody Boolean loginIdValidation(String loginId) {
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

    @PostMapping(value = "/forgetId/getCertificate")
    public @ResponseBody String getCertificateNumberByName(String name, String phone,
                                                           RedirectAttributes redirectAttributes) throws CoolsmsException {
        if(memberService.findLoginIdByName(name, phone)==null) return null;
        redirectAttributes.addAttribute("phone", phone);

        //return certificationService.PhoneNumberCheck(phone);
        return "1234";
    }

    @PostMapping(value = "/forgetId/findId")
    public String findId(String name, String phone , RedirectAttributes redirectAttributes) {
        List<String> loginId = memberService.findLoginIdByName(name, phone);
        redirectAttributes.addFlashAttribute("loginId", loginId);
        return "redirect:/members/forgetId/success";
    }

    @GetMapping(value = "/forgetId/success")
    public String findIdSuccess(Model model) {
        return "/user/forget/findId";
    }


    @GetMapping(value = "/forgetPassword")
    public String forgetPassword(Model model) {
        return "/user/forget/forgetPassword";
    }

    @PostMapping(value = "/forgetPassword/getCertificate")
    public @ResponseBody String getCertificateNumberByLoginId(String loginId, String phone,
                                                              RedirectAttributes redirectAttributes) throws CoolsmsException {
        if(memberService.findMemberByLoginId(loginId, phone)==null) return null;
        redirectAttributes.addAttribute("phone", phone);

        //return certificationService.PhoneNumberCheck(phone);
        return "1234";
    }

    @PostMapping(value = "/forgetPassword/validationId")
    public String validationId(String loginId, RedirectAttributes redirectAttributes) {

        Member member = memberService.findUser(loginId);
        if(member==null) return "redirect:/members/forgetPassword";

        redirectAttributes.addFlashAttribute("loginId", loginId);

        return "redirect:/members/forgetPassword/changePassword";
    }

    @GetMapping(value = "/forgetPassword/changePassword")
    public String changePasswordForm(Model model) {
        String loginId = (String) model.getAttribute("loginId");

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



    @PostMapping(value = "/findDeliveryInfo") @Secured({"ROLE_USER"})
    public @ResponseBody DeliveryInfoDto findDeliveryInfo(@AuthenticationPrincipal LoginUserDetails member,
                                                          @RequestParam(name="delivery_id") Long deliveryId) {
        return new DeliveryInfoDto(memberRepository.findDeliveryinfo(member.getUsername(), deliveryId).orElseThrow(()-> new NoSuchElementException("존재하지 않는 정보입니다.")));
    }

    @PostMapping(value = "/updateRole")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity updateRole(@RequestParam("loginId") String loginId, @RequestParam("newRole") String newRole) {
        memberService.updateRole(loginId, MemberRole.valueOf(newRole));
        return new ResponseEntity("권한을 성공적으로 수정했습니다.", HttpStatus.OK);
    }

}
