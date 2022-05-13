package dare.daremall.controller.member;

import dare.daremall.controller.member.forget.ChangePasswordForm;
import dare.daremall.controller.member.forget.ForgetIdDto;
import dare.daremall.domain.Member;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.CertificationService;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CertificationService certificationService;

    @GetMapping(value = "/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/loginForm";
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@Valid LoginForm form) {

        return "login";
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

    @GetMapping(value = "/getCertificateNumber")
    public @ResponseBody String getCertificateNumber(@RequestParam(value = "phone", required = false) String phone,
                                                     RedirectAttributes redirectAttributes) throws CoolsmsException {
        redirectAttributes.addAttribute("phone", phone);

        //return certificationService.PhoneNumberCheck(phone);
        return "1234";
    }

    @GetMapping(value = "/forgetId")
    public String forgetId(Model model) {
        return "/user/forget/forgetId";
    }

    @GetMapping(value = "/findId")
    public @ResponseBody String findId(@RequestParam("phone") String phone) {
        return memberService.findLoginId(phone);
    }

    @GetMapping(value = "/findIdSuccess")
    public String findIdSuccess(Model model, @RequestParam("loginId") String loginId) {
        model.addAttribute("loginId", loginId);
        return "/user/forget/findId";
    }

    @GetMapping(value = "/forgetPassword")
    public String forgetPassword(Model model) {
        return "/user/forget/forgetPassword";
    }

    @GetMapping(value = "/forgetPassword/changePassword")
    public String changePasswordForm(Model model, @RequestParam("loginId") String loginId) {

        Member member = memberService.findUser(loginId);
        if(member==null) return "redirect:/user/forget/forgetPassword";

        model.addAttribute("changePasswordForm", new ChangePasswordForm(loginId));

        return "/user/forget/changePassword";
    }

    @PostMapping(value = "/changePassword")
    public String changePassword(ChangePasswordForm form) {

        memberService.passwordChange(form);

        return "redirect:/members/findPasswordSuccess";
    }

    @GetMapping(value = "/findPasswordSuccess")
    public String findPasswordSuccess(Model model) {
        return "/user/forget/findPassword";
    }

}
