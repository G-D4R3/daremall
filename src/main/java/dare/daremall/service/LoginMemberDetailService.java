package dare.daremall.service;

import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.Member;
import dare.daremall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginMemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByLoginId(username).orElseGet(null);
        if(findMember!=null) {
            return new LoginUserDetails(findMember);
        }
        return null;
    }
}
