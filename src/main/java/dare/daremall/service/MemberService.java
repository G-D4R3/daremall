package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.forget.ChangePasswordForm;
import dare.daremall.domain.Address;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Item;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final ItemRepository itemRepository;
    private final BaggedItemRepository baggedItemRepository;

    @Transactional
    public Long join(MemberSignupRequestDto memberDto) {
        //validateDuplicateMember(memberDto);
        Member newMember = new Member(memberDto.getName(),
                                        memberDto.getLoginId(),
                                        memberDto.getPassword(),
                                        memberDto.getPhone(),
                                        new Address(memberDto.getZipcode(),
                                                memberDto.getStreet(),
                                                memberDto.getDetail()));
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

    public Member findUser(String loginId) {
        return memberRepository.findByLoginId(loginId).orElse(null);
    }

    @Transactional
    public void addShoppingBag(Long itemId, String loginId, int count) {
        Item item = itemRepository.findById(itemId).orElse(null); //  나중에 예외처리 필요
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        BaggedItem baggedItem = BaggedItem.createBaggedItem(member, item, item.getPrice(), count);
        member.addBaggedItem(baggedItem);
    }

    @Transactional
    public void removeShoppingBag(String loginId, Long baggedItemId) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        BaggedItem baggedItem = baggedItemRepository.findById(baggedItemId);
        member.removeBaggedItem(baggedItem);
        baggedItemRepository.remove(baggedItemId);
    }

    @Transactional
    public void updateBaggedItemCount(Long bagItemId, int count) {
        BaggedItem item = baggedItemRepository.findById(bagItemId);
        item.setCount(count);
    }

    @Transactional
    public void updateBaggedItemCheck(Long bagItemId) {
        BaggedItem item = baggedItemRepository.findById(bagItemId);
        item.setChecked(!item.getChecked());
    }

    public List<BaggedItem> getSelectedBaggedItem(String loginId) {
        return baggedItemRepository.findSelected(loginId);
    }


    public List<String> findLoginIdByName(String name, String phone) {
        return memberRepository.findLoginIdByName(name, phone).stream().map(m->m.getLoginId()).collect(Collectors.toList());
    }

    @Transactional
    public void passwordChange(ChangePasswordForm form) {
        Member member = memberRepository.findByLoginId(form.getLoginId()).orElse(null);
        member.setPassword(form.getPassword());
        member.encryptPassword(passwordEncoder);
    }

    public Member findMemberByLoginId(String loginId, String phone) {
        return memberRepository.findMemberByLoginId(loginId, phone).orElse(null);
    }
}

