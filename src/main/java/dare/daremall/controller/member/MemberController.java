package dare.daremall.controller.member;

import dare.daremall.controller.LoginForm;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Item;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @GetMapping(value = "/members/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/loginForm";
    }

    @PostMapping(value = "/members/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@Valid LoginForm form) {

        return "login";
    }

    @GetMapping(value = "/members/new")
    public String createMemberForm(Model model) {
        model.addAttribute("memberDto", new MemberSignupRequestDto());
        return "user/createMemberForm";
    }

    @PostMapping(value = "/members/new")
    public String createMember(@Validated @ModelAttribute("memberDto") MemberSignupRequestDto memberDto, BindingResult result) {
        if(result.hasErrors()) {
            return "user/createMemberForm";
        }
        memberService.join(memberDto);
        return "redirect:/";
    }

//    @PostMapping(value = "signup", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Long signup(@Valid MemberSignupRequestDto memberDto, BindingResult result) {
//        return memberService.join(memberDto);
//    }

}
