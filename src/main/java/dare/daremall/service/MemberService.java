package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.forget.ChangePasswordForm;
import dare.daremall.controller.member.mypage.DeliveryInfoForm;
import dare.daremall.controller.member.mypage.UpdateDeliveryInfoForm;
import dare.daremall.domain.Address;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.DeliveryInfo;
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
        validateDuplicateMember(memberDto);
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
        List<Member> loginId = memberRepository.findLoginIdByName(member.getName(), member.getPhone());
        if(loginId.size()>=3) {
            throw new IllegalStateException("같은 이름, 휴대폰 번호 조합으로 3회까지만 회원가입을 시도할 수 있습니다.");
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

    @Transactional
    public void updateUserInfo(String loginId, String phone, String zipcode, String street, String detail) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        member.setPhone(phone);
        member.setAddress(new Address(zipcode, street, detail));
    }

    @Transactional
    public void addDeliveryInfo(String loginId, DeliveryInfoForm deliveryInfoForm) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setName(deliveryInfoForm.getName());
        deliveryInfo.setPhone(deliveryInfoForm.getPhone());
        deliveryInfo.setNickname(deliveryInfoForm.getNickname());
        deliveryInfo.setAddress(new Address(deliveryInfoForm.getZipcode(), deliveryInfoForm.getStreet(), deliveryInfoForm.getDetail()));
        deliveryInfo.setIsDefault(deliveryInfoForm.getIsDefault());
        member.addDelivery(deliveryInfo);
    }

    @Transactional
    public void deleteDeliveryInfo(String loginId, Long deliveryId) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        DeliveryInfo deliveryInfo = memberRepository.findDeliveryinfo(loginId, deliveryId).orElse(null);

        if(deliveryInfo!=null) {
            if(deliveryInfo.getIsDefault()==true) {
                throw new IllegalStateException("기본 배송지는 삭제할 수 없습니다.");
            }
            else member.removeDelivery(deliveryInfo);
        }

    }

    @Transactional
    public void updateDeliveryInfo(String loginId, UpdateDeliveryInfoForm updateDeliveryInfoForm) {
        DeliveryInfo deliveryInfo = memberRepository.findDeliveryinfo(loginId, updateDeliveryInfoForm.getId()).orElse(null);
        deliveryInfo.setName(updateDeliveryInfoForm.getName());
        deliveryInfo.setPhone(updateDeliveryInfoForm.getPhone());
        deliveryInfo.setNickname(updateDeliveryInfoForm.getNickname());
        deliveryInfo.setAddress(new Address(updateDeliveryInfoForm.getZipcode(), updateDeliveryInfoForm.getStreet(), updateDeliveryInfoForm.getDetail()));
        deliveryInfo.setIsDefault(updateDeliveryInfoForm.getIsDefault());
        if(deliveryInfo.getIsDefault()==true) {
            DeliveryInfo defaultDeliveryInfo = memberRepository.findDefaultDeliveryInfo(loginId).orElse(null);
            defaultDeliveryInfo.setIsDefault(false);
        }
    }
}

