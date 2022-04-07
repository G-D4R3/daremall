package dare.daremall.service;

import dare.daremall.controller.member.MemberSignupRequestDto;
import dare.daremall.domain.Address;
import dare.daremall.domain.Member;
import dare.daremall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(MemberSignupRequestDto memberDto) {
        //validateDuplicateMember(memberDto);
        Member newMember = new Member(memberDto.getName(),
                                        memberDto.getLoginId(),
                                        memberDto.getPassword(),
                                        new Address(memberDto.getCity(),
                                                memberDto.getStreet(),
                                                memberDto.getZipcode()));
        newMember.encryptPassword(passwordEncoder);
        memberRepository.save(newMember);
        return newMember.getId();
    }

    private void validateDuplicateMember(MemberSignupRequestDto member) {
        Member dupicateMember = memberRepository.findByLoginId(member.getLoginId()).orElse(null);
        if(dupicateMember != null) {
            throw new IllegalStateException("이미 사용중인 아이디입니다.");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id).orElse(null); // Optional로 받아서 처리하기
    }
}
