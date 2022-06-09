package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.mypage.ChangePasswordForm;
import dare.daremall.controller.member.mypage.DeliveryInfoForm;
import dare.daremall.controller.member.mypage.UpdateDeliveryInfoForm;
import dare.daremall.domain.*;
import dare.daremall.domain.item.Item;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.LikeItemRepository;
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
    private final LikeItemRepository likeItemRepository;


    @Transactional
    public Long join(MemberSignupRequestDto memberDto){
        validateDuplicateMember(memberDto);
        validateNoMoreThan3(memberDto);
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

    private void validateNoMoreThan3(MemberSignupRequestDto member) {
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
        memberRepository.save(member);
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
        if(item.getItem().getStockQuantity() < count) {
            throw new IllegalStateException("재고 수량을 초과했습니다.");
        }
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
    public void passwordChange(String loginId, String newPassword) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        member.setPassword(newPassword);
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
        memberRepository.save(member);
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
        memberRepository.save(member);

    }

    @Transactional
    public void updateDeliveryInfo(String loginId, UpdateDeliveryInfoForm updateDeliveryInfoForm) {

        Member findMember = memberRepository.findByLoginId(loginId).orElse(null);
        DeliveryInfo deliveryInfo = memberRepository.findDeliveryinfo(loginId, updateDeliveryInfoForm.getId()).orElse(null);
        DeliveryInfo defaultDeliveryInfo = findMember.getDefaultDelivery();

        if(defaultDeliveryInfo == null){
            throw new IllegalStateException("배송지 수정에 문제가 생겼습니다.");
        }

        if(defaultDeliveryInfo.getId() == deliveryInfo.getId() && updateDeliveryInfoForm.getIsDefault() == false) {
            throw new IllegalStateException("기본 배송지는 삭제할 수 없습니다.");
        }
        else {
            deliveryInfo.setName(updateDeliveryInfoForm.getName());
            deliveryInfo.setPhone(updateDeliveryInfoForm.getPhone());
            deliveryInfo.setNickname(updateDeliveryInfoForm.getNickname());
            deliveryInfo.setAddress(new Address(updateDeliveryInfoForm.getZipcode(), updateDeliveryInfoForm.getStreet(), updateDeliveryInfoForm.getDetail()));
        }

        if(defaultDeliveryInfo.getId() != deliveryInfo.getId() && updateDeliveryInfoForm.getIsDefault() == true) {
            deliveryInfo.setIsDefault(true);
            defaultDeliveryInfo.setIsDefault(false);
            findMember.setDefaultDelivery(deliveryInfo);
        }
        else if (defaultDeliveryInfo.getId() != deliveryInfo.getId() && updateDeliveryInfoForm.getIsDefault() == false){
            deliveryInfo.setIsDefault(false);
        }

        memberRepository.save(findUser(loginId));
    }

    @Transactional
    public void selectAllBagItem(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        for(BaggedItem baggedItem:member.getShoppingBag()) {
            baggedItem.setChecked(true);
        }
    }

    @Transactional
    public void changeLikeItem(String loginId, Long itemId) {

        LikeItem findLikeItem = likeItemRepository.findByIds(loginId, itemId).orElse(null);
        Item findItem = itemRepository.findById(itemId).orElse(null);
        Member findMember = memberRepository.findByLoginId(loginId).orElse(null);

        if(findLikeItem==null) {
            LikeItem likeItem = new LikeItem();
            likeItem.setItem(findItem);

            findMember.addLikeItem(likeItem);
        }
        else {
            findMember.removeLikeItem(findLikeItem);
            likeItemRepository.remove(findLikeItem.getId());
        }
        memberRepository.save(findMember);
    }
}

