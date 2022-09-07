package dare.daremall.member.services;

import dare.daremall.member.domains.*;
import dare.daremall.member.dtos.authDtos.MemberSignupRequestDto;
import dare.daremall.member.dtos.mypageDtos.DeliveryInfoForm;
import dare.daremall.member.dtos.mypageDtos.UpdateDeliveryInfoForm;
import dare.daremall.item.domains.Item;
import dare.daremall.item.domains.ItemStatus;
import dare.daremall.core.exception.CannotRegisterMemberException;
import dare.daremall.core.exception.NotEnoughStockException;
import dare.daremall.member.repositories.BaggedItemRepository;
import dare.daremall.item.repositories.ItemRepository;
import dare.daremall.member.repositories.LikeItemRepository;
import dare.daremall.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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

    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다.")); // Optional로 받아서 처리하기
    }

    public Member findUser(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    public Member validateDuplicateLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElse(null);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원가입
     **/

    @Transactional
    public Long join(MemberSignupRequestDto memberSignupRequestDto) { // null check x
        validateDuplicateMember(memberSignupRequestDto);
        validateNoMoreThan3(memberSignupRequestDto);
        Member newMember = new Member(memberSignupRequestDto.getName(),
                memberSignupRequestDto.getLoginId(),
                memberSignupRequestDto.getPassword(),
                memberSignupRequestDto.getPhone(),
                new Address(memberSignupRequestDto.getZipcode(),
                        memberSignupRequestDto.getStreet(),
                        memberSignupRequestDto.getDetail()));
        newMember.encryptPassword(passwordEncoder);
        memberRepository.save(newMember);
        return newMember.getId();
    }

    private void validateDuplicateMember(MemberSignupRequestDto memberSignupResuetDto) {
        Member duplicateMember = memberRepository.findByLoginId(memberSignupResuetDto.getLoginId()).orElse(null);
        if (duplicateMember != null) {
            throw new CannotRegisterMemberException("이미 사용중인 아이디입니다.");
        }
    }

    private void validateNoMoreThan3(MemberSignupRequestDto memberSignupResuetDto) {
        List<Member> loginIds = memberRepository.findLoginIdByNameAndPhone(memberSignupResuetDto.getName(), memberSignupResuetDto.getPhone());
        if (loginIds.size() >= 3) {
            throw new CannotRegisterMemberException("같은 이름, 휴대폰 번호 조합으로 3회까지만 회원가입을 시도할 수 있습니다.");
        }
    }

    /** **/

    /**
     * 장바구니
     **/

    @Transactional
    public void addItemToShoppingBag(Long itemId, String loginId, int count) {
        if (count <= 0) {
            throw new IllegalStateException("상품을 장바구니에 추가할 수 없습니다.");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        if (item.getItemStatus() != ItemStatus.FOR_SALE) {
            throw new IllegalStateException("판매 중단된 상품은 장바구니에 추가할 수 없습니다.");
        }
        if (item.getStockQuantity() < count) {
            throw new NotEnoughStockException("재고 수량을 초과했습니다.");
        }

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        BaggedItem baggedItem = BaggedItem.createBaggedItem(member, item, item.getPrice(), count);
        member.addBaggedItem(baggedItem);
        memberRepository.save(member);
    }

    @Transactional
    public void removeItemFromShoppingBag(String loginId, Long baggedItemId) {
        Member member = memberRepository.findByLoginId(loginId).get();
        BaggedItem baggedItem = baggedItemRepository.findById(baggedItemId).orElseThrow(() -> new NoSuchElementException("이미 장바구니에서 삭제되었습니다."));

        member.removeBaggedItem(baggedItem);
        memberRepository.save(member);
    }

    @Transactional
    public void updateBaggedItemCount(Long bagItemId, int count) {
        if (count <= 0) {
            throw new IllegalStateException("장바구니 수량을 변경할 수 없습니다.");
        }
        BaggedItem item = baggedItemRepository.findById(bagItemId).orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        if (item.getItem().getStockQuantity() < count) {
            throw new NotEnoughStockException("재고 수량을 초과했습니다.");
        }
        item.setCount(count);
    }

    @Transactional
    public void updateBaggedItemCheck(Long bagItemId) {
        BaggedItem item = baggedItemRepository.findById(bagItemId).orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        item.setChecked(!item.getChecked());
    }

    public List<BaggedItem> getSelectedBaggedItem(String loginId) {
        return baggedItemRepository.findSelectedBaggedItem(loginId);
    }

    @Transactional
    public void selectAllBagItem(String loginId) {
        baggedItemRepository.setAllCheckedBaggedItem(loginId);
    }

    /** **/


    /**
     * 아이디 찾기 사용자 조회
     **/
    public List<String> findLoginIdByName(String name, String phone) {
        return memberRepository.findLoginIdByNameAndPhone(name, phone).stream().map(m -> m.getLoginId()).collect(Collectors.toList());
    }

    /**
     * 비밀번호 찾기
     **/

    public Member findMemberByLoginIdAndPhoneNumber(String loginId, String phone) {
        return memberRepository.findMemberByLoginId(loginId, phone).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void changePassword(String loginId, String newPassword) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        member.setPassword(newPassword);
        member.encryptPassword(passwordEncoder);
    }
    /** **/

    /**
     * 회원 정보 관리 페이지
     **/

    @Transactional
    public void updateUserInfo(String loginId, String phone, String zipcode, String street, String detail) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        member.setPhone(phone);
        member.setAddress(new Address(zipcode, street, detail));
    }

    @Transactional
    public void addDeliveryInfo(String loginId, DeliveryInfoForm deliveryInfoForm) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setName(deliveryInfoForm.getName());
        deliveryInfo.setPhone(deliveryInfoForm.getPhone());
        deliveryInfo.setNickname(deliveryInfoForm.getNickname());
        deliveryInfo.setAddress(new Address(deliveryInfoForm.getZipcode(), deliveryInfoForm.getStreet(), deliveryInfoForm.getDetail()));
        deliveryInfo.setIsDefault(deliveryInfoForm.getIsDefault());

        DeliveryInfo defaultDelivery = memberRepository.findDefaultDeliveryInfo(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 정보입니다."));
        if (deliveryInfo.getIsDefault() == true) {
            defaultDelivery.setIsDefault(false);
        }

        member.addDelivery(deliveryInfo);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteDeliveryInfo(String loginId, Long deliveryId) {
        DeliveryInfo deliveryInfo = memberRepository.findDeliveryInfo(loginId, deliveryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 정보입니다."));

        if (deliveryInfo != null) {
            if (deliveryInfo.getIsDefault() == true) {
                throw new IllegalStateException("기본 배송지는 삭제할 수 없습니다.");
            } else {
                Member member = memberRepository.findByLoginId(loginId).get();
                member.removeDelivery(deliveryInfo);
                memberRepository.save(member);
            }
        }
    }

    @Transactional
    public void updateDeliveryInfo(String loginId, UpdateDeliveryInfoForm updateDeliveryInfoForm) {

        //Member findMember = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        DeliveryInfo deliveryInfo = memberRepository.findDeliveryInfo(loginId, updateDeliveryInfoForm.getId()).orElse(null);

        if (deliveryInfo == null) {
            throw new IllegalStateException("배송지 수정에 문제가 생겼습니다.");
        }

        if (deliveryInfo.getIsDefault() == true && updateDeliveryInfoForm.getIsDefault() == false) {
            throw new IllegalStateException("기본 배송지는 삭제할 수 없습니다.");
        }

        deliveryInfo.setName(updateDeliveryInfoForm.getName());
        deliveryInfo.setPhone(updateDeliveryInfoForm.getPhone());
        deliveryInfo.setNickname(updateDeliveryInfoForm.getNickname());
        deliveryInfo.setAddress(new Address(updateDeliveryInfoForm.getZipcode(), updateDeliveryInfoForm.getStreet(), updateDeliveryInfoForm.getDetail()));

        if (deliveryInfo.getIsDefault() == false && updateDeliveryInfoForm.getIsDefault() == true) {
            DeliveryInfo defaultDeliveryInfo = memberRepository.findDefaultDeliveryInfo(loginId).orElse(null);
            deliveryInfo.setIsDefault(true);
            defaultDeliveryInfo.setIsDefault(false);
        } else if (deliveryInfo.getIsDefault() == false && updateDeliveryInfoForm.getIsDefault() == false) {
            deliveryInfo.setIsDefault(false);
        }

        //memberRepository.save(findMember);
    }
    /** **/


    /**
     * 상품 좋아요
     **/

    public List<LikeItem> getLikes(String loginId) {
        return memberRepository.getLikes(loginId);
    }

    @Transactional
    public void updateLikeItem(String loginId, Long itemId) {

        LikeItem findLikeItem = likeItemRepository.findOne(loginId, itemId).orElse(null);
        //Item findItem = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        Member findMember = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        if (findLikeItem == null) {
            Item findItem = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

            LikeItem likeItem = new LikeItem();
            likeItem.setItem(findItem);
            findMember.addLikeItem(likeItem);
        } else {
            findMember.removeLikeItem(findLikeItem);
            //likeItemRepository.remove(findLikeItem.getId());
        }
        memberRepository.save(findMember);
    }


    /**
     * 관리자 페이지 - /member
     */
    public List<Member> findMembersByString(String search) {
        return memberRepository.findMembersByString(search);
    }

    public List<Member> findAdmins() {
        return memberRepository.findAdmins();
    }

    @Transactional
    public void updateMemberRole(String loginId, MemberRole newRole) {
        Member member = memberRepository.findByLoginId(loginId).get();
        member.setRole(newRole);
        memberRepository.save(member);

    }

    @Transactional
    public void deleteBaggedItemByItemId(Long itemId) {
        memberRepository.removeBaggedItemByItemId(itemId);
    }

    /** **/
}

