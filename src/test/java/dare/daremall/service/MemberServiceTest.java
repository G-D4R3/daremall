package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.mypage.DeliveryInfoForm;
import dare.daremall.controller.member.mypage.UpdateDeliveryInfoForm;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.DeliveryInfo;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.exception.CannotRegisterMemberException;
import dare.daremall.exception.NotEnoughStockException;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired PasswordEncoder passwordEncoder;

    @Autowired ItemService itemService;
    @Autowired BaggedItemRepository baggedItemRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입_성공() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");

        // when
        Long id = memberService.join(memberDto);

        // then
        Assertions.assertThat(memberDto.getLoginId()).isEqualTo(memberService.findOne(id).getLoginId());
    }


    @Test
    public void 중복아이디_회원가입_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");

        // when
        memberService.join(memberDto);
        CannotRegisterMemberException e = assertThrows(CannotRegisterMemberException.class, () -> memberService.join(memberDto));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 사용중인 아이디입니다.");
    }

    @Test
    public void 같은_휴대폰번호_이름조합_3회까지_회원가입_가능() {
        // given
        MemberSignupRequestDto memberDto1 = new MemberSignupRequestDto();
        memberDto1.setName("지창민");
        memberDto1.setLoginId("member1");
        memberDto1.setPassword("pass1234");
        memberDto1.setPhone("010-1111-2222");
        memberDto1.setZipcode("00000");
        memberDto1.setStreet("강가 1번길");
        memberDto1.setDetail("어딘가");

        MemberSignupRequestDto memberDto2 = new MemberSignupRequestDto();
        memberDto2.setName("지창민");
        memberDto2.setLoginId("member2");
        memberDto2.setPassword("pass1234");
        memberDto2.setPhone("010-1111-2222");
        memberDto2.setZipcode("00000");
        memberDto2.setStreet("강가 1번길");
        memberDto2.setDetail("어딘가");

        MemberSignupRequestDto memberDto3 = new MemberSignupRequestDto();
        memberDto3.setName("지창민");
        memberDto3.setLoginId("member3");
        memberDto3.setPassword("pass1234");
        memberDto3.setPhone("010-1111-2222");
        memberDto3.setZipcode("00000");
        memberDto3.setStreet("강가 1번길");
        memberDto3.setDetail("어딘가");

        MemberSignupRequestDto memberDto4 = new MemberSignupRequestDto();
        memberDto4.setName("지창민");
        memberDto4.setLoginId("member4");
        memberDto4.setPassword("pass1234");
        memberDto4.setPhone("010-1111-2222");
        memberDto4.setZipcode("00000");
        memberDto4.setStreet("강가 1번길");
        memberDto4.setDetail("어딘가");

        // when
        memberService.join(memberDto1);
        memberService.join(memberDto2);
        memberService.join(memberDto3);

        CannotRegisterMemberException e = assertThrows(CannotRegisterMemberException.class, () -> memberService.join(memberDto4));

        // then
        assertThat(e.getMessage()).isEqualTo("같은 이름, 휴대폰 번호 조합으로 3회까지만 회원가입을 시도할 수 있습니다.");

    }

    @Test
    public void 회원조회() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");

        // when
        memberService.join(memberDto);
        assertThat(memberService.findUser(memberDto.getLoginId()).getLoginId()).isEqualTo(memberDto.getLoginId());
        NoSuchElementException e = assertThrows(NoSuchElementException.class, ()->memberService.findUser("notMember"));

        // then
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    public void 비밀번호_변경_성공() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.passwordChange(findMember.getLoginId(), "newPass1234");

        // then
        assertThat(passwordEncoder.matches("newPass1234", findMember.getPassword())).isEqualTo(true);

    }

    // 비밀번호 변경 실패 -> controller

    @Test
    public void 장바구니_추가() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        // when
        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(albumId, findMember.getLoginId(), 2);
        Item findItem = itemService.findOne(albumId);

        // then
        assertThat(findMember.getShoppingBag().stream().map(b->b.getItem()).collect(Collectors.toList()).contains(findItem)).isTrue();
        assertThat(findMember.getShoppingBag().get(0).getCount()).isEqualTo(2);
        assertThat(findMember.getShoppingBag().stream().mapToInt(bi->bi.getTotalPrice()).sum()).isEqualTo(2000);
        assertThat(findItem.getStockQuantity()).isEqualTo(10);

    }

    // 장바구니 추가 실패 -> 없는 상품, 재고 수량 초과, 상품 수량 음수

    @Test
    public void 장바구니_추가_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");
        long albumId = itemService.saveItem(album);

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("NOT_FOR_SALE");
        long bookId = itemService.saveItem(book);

        // when
        NotEnoughStockException e1 = assertThrows(NotEnoughStockException.class, ()-> memberService.addShoppingBag(albumId, memberDto.getLoginId(), 11));
        IllegalStateException e2 = assertThrows(IllegalStateException.class, ()-> memberService.addShoppingBag(bookId, memberDto.getLoginId(), 1));
        IllegalStateException e3 = assertThrows(IllegalStateException.class, ()-> memberService.addShoppingBag(albumId, memberDto.getLoginId(), -1));


        // then
        assertThat(e1.getMessage()).isEqualTo("재고 수량을 초과했습니다.");
        assertThat(e2.getMessage()).isEqualTo("판매 중단된 상품은 장바구니에 추가할 수 없습니다.");
        assertThat(e3.getMessage()).isEqualTo("상품을 장바구니에 추가할 수 없습니다.");
    }

    @Test
    public void 장바구니_삭제() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(albumId, findMember.getLoginId(), 2);

        // when
        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);
        memberService.removeShoppingBag(baggedItem.getId());

        // then
        assertThat(findMember.getShoppingBag().size()).isEqualTo(0);

    }

    // 장바구니 삭제 실패 -> 이미 삭제한 상품

    @Test
    public void 장바구니_삭제_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(albumId, findMember.getLoginId(), 2);

        // when
        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        long bagItemId = findMember.getShoppingBag().get(0).getId();
        memberService.removeShoppingBag(bagItemId);

        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> memberService.removeShoppingBag(bagItemId));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 장바구니에서 삭제되었습니다.");

    }


    @Test
    public void 장바구니_수량_수정() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(albumId, findMember.getLoginId(), 2);

        // when
        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);
        memberService.updateBaggedItemCount(baggedItem.getId(), 5);

        // then
        assertThat(baggedItemRepository.findById(baggedItem.getId()).getCount()).isEqualTo(5);

    }

    // 장바구니 수정 실패 -> 장바구니 상품 없음, 상품 재고 수량 초과, 음수

    @Test
    public void 장바구니_수정_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");;
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(albumId, findMember.getLoginId(), 2);

        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);

        // when
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class, () -> memberService.updateBaggedItemCount(-1L, 1));
        IllegalStateException e2 = assertThrows(IllegalStateException.class, () -> memberService.updateBaggedItemCount(baggedItem.getId(), -1));
        NotEnoughStockException e3 = assertThrows(NotEnoughStockException.class, () -> memberService.updateBaggedItemCount(baggedItem.getId(), 11));

        // then
        assertThat(e1.getMessage()).isEqualTo("상품을 찾을 수 없습니다.");
        assertThat(e2.getMessage()).isEqualTo("장바구니 수량을 변경할 수 없습니다.");
        assertThat(e3.getMessage()).isEqualTo("재고 수량을 초과했습니다.");

    }


    @Test
    public void 주소지_변경_성공() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        memberService.updateUserInfo(memberDto.getLoginId(), "010-3333-4444", "11111", "강가 2번길", "어디엔가");
        Member findMember = memberService.findUser(memberDto.getLoginId());

        // then
        assertThat(findMember.getPhone()).isEqualTo("010-3333-4444");
        assertThat(findMember.getAddress().getZipcode()).isEqualTo("11111");
        assertThat(findMember.getAddress().getStreet()).isEqualTo("강가 2번길");
        assertThat(findMember.getAddress().getDetail()).isEqualTo("어디엔가");

    } // valid check


    @Test
    public void 배송지_추가() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        DeliveryInfoForm deliveryInfoForm = new DeliveryInfoForm();
        deliveryInfoForm.setName("지창민");
        deliveryInfoForm.setNickname("학교");
        deliveryInfoForm.setPhone("010-1111-2222");
        deliveryInfoForm.setZipcode("11111");
        deliveryInfoForm.setStreet("학교앞 1번길");
        deliveryInfoForm.setDetail("어딘가");
        deliveryInfoForm.setIsDefault(false);
        memberService.addDeliveryInfo(memberDto.getLoginId(), deliveryInfoForm);
        Member findMember = memberService.findUser(memberDto.getLoginId());

        // then
        assertThat(findMember.getDeliveryInfos().size()).isEqualTo(2);
        assertThat(findMember.getDeliveryInfos().get(1).getIsDefault()).isFalse();


    }



    // 아래 실패 로직 추가로 생각하기

    @Test
    public void 기본_배송지_수정_성공() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        Member findMember = memberService.findUser(memberDto.getLoginId());
        DeliveryInfo deliveryInfo = memberRepository.findDefaultDeliveryInfo(findMember.getLoginId()).orElse(null);

        UpdateDeliveryInfoForm updateDeliveryInfoForm = new UpdateDeliveryInfoForm();
        updateDeliveryInfoForm.setId(deliveryInfo.getId());
        updateDeliveryInfoForm.setName("김선우");
        updateDeliveryInfoForm.setNickname("친구");
        updateDeliveryInfoForm.setPhone("010-3333-4444");
        updateDeliveryInfoForm.setZipcode("11111");
        updateDeliveryInfoForm.setStreet("학교뒤 1번길");
        updateDeliveryInfoForm.setDetail("어딘가");
        updateDeliveryInfoForm.setIsDefault(true);

        memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm);
        findMember = memberService.findUser(memberDto.getLoginId());

        // then
        DeliveryInfo updatedDefaultDeliveryInfo = memberRepository.findDefaultDeliveryInfo(findMember.getLoginId()).orElse(null);
        assertThat(findMember.getDeliveryInfos().size()).isEqualTo(1);
        assertThat(updatedDefaultDeliveryInfo.getId()).isEqualTo(deliveryInfo.getId());
        assertThat(updatedDefaultDeliveryInfo.getName()).isEqualTo("김선우");
        assertThat(updatedDefaultDeliveryInfo.getNickname()).isEqualTo("친구");
        assertThat(updatedDefaultDeliveryInfo.getPhone()).isEqualTo("010-3333-4444");
        assertThat(updatedDefaultDeliveryInfo.getAddress().getZipcode()).isEqualTo("11111");
        assertThat(updatedDefaultDeliveryInfo.getAddress().getStreet()).isEqualTo("학교뒤 1번길");
        assertThat(updatedDefaultDeliveryInfo.getAddress().getDetail()).isEqualTo("어딘가");
        assertThat(updatedDefaultDeliveryInfo.getIsDefault()).isTrue();

    }


    @Test
    public void 기본_배송지_수정_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        Member findMember = memberService.findUser(memberDto.getLoginId());
        DeliveryInfo deliveryInfo = memberRepository.findDefaultDeliveryInfo(findMember.getLoginId()).orElse(null);

        UpdateDeliveryInfoForm updateDeliveryInfoForm = new UpdateDeliveryInfoForm();
        updateDeliveryInfoForm.setId(deliveryInfo.getId());
        updateDeliveryInfoForm.setName("김선우");
        updateDeliveryInfoForm.setNickname("친구");
        updateDeliveryInfoForm.setPhone("010-3333-4444");
        updateDeliveryInfoForm.setZipcode("11111");
        updateDeliveryInfoForm.setStreet("학교뒤 1번길");
        updateDeliveryInfoForm.setDetail("어딘가");
        updateDeliveryInfoForm.setIsDefault(false);

        IllegalStateException e1 = assertThrows(IllegalStateException.class, () -> memberService.updateDeliveryInfo(memberDto.getLoginId(), updateDeliveryInfoForm));

        findMember.removeDelivery(findMember.getDeliveryInfos().get(0));
        memberRepository.save(findMember);

        findMember = memberService.findUser(memberDto.getLoginId());
        IllegalStateException e2 = assertThrows(IllegalStateException.class, () -> memberService.updateDeliveryInfo(memberDto.getLoginId(), updateDeliveryInfoForm));

        // then
        assertThat(e1.getMessage()).isEqualTo("기본 배송지는 삭제할 수 없습니다.");
        assertThat(e2.getMessage()).isEqualTo("배송지 수정에 문제가 생겼습니다.");

    }


    @Test
    public void 기본_배송지_삭제_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        DeliveryInfo deliveryInfo = memberRepository.findDefaultDeliveryInfo(findMember.getLoginId()).orElse(null);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.deleteDeliveryInfo(findMember.getLoginId(), deliveryInfo.getId()));

        // then
        assertThat(e.getMessage()).isEqualTo("기본 배송지는 삭제할 수 없습니다.");

    }



    @Test
    public void 배송지_수정_성공() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when
        DeliveryInfoForm deliveryInfoForm = new DeliveryInfoForm();
        deliveryInfoForm.setName("지창민");
        deliveryInfoForm.setNickname("학교");
        deliveryInfoForm.setPhone("010-1111-2222");
        deliveryInfoForm.setZipcode("11111");
        deliveryInfoForm.setStreet("학교앞 1번길");
        deliveryInfoForm.setDetail("어딘가");
        deliveryInfoForm.setIsDefault(false);
        memberService.addDeliveryInfo(memberDto.getLoginId(), deliveryInfoForm); // 배송지 추가

        Member findMember = memberService.findUser(memberDto.getLoginId());
        DeliveryInfo deliveryInfo = findMember.getDeliveryInfos().get(1);

        UpdateDeliveryInfoForm updateDeliveryInfoForm = new UpdateDeliveryInfoForm();
        updateDeliveryInfoForm.setId(deliveryInfo.getId());
        updateDeliveryInfoForm.setName("김선우");
        updateDeliveryInfoForm.setNickname("친구");
        updateDeliveryInfoForm.setPhone("010-3333-4444");
        updateDeliveryInfoForm.setZipcode("11111");
        updateDeliveryInfoForm.setStreet("학교뒤 1번길");
        updateDeliveryInfoForm.setDetail("어딘가");
        updateDeliveryInfoForm.setIsDefault(false);

        memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm); // 배송지 수정
        findMember = memberService.findUser(memberDto.getLoginId());


        // then
        DeliveryInfo updatedDeliveryInfo = findMember.getDeliveryInfos().get(1);
        assertThat(updatedDeliveryInfo.getId()).isEqualTo(deliveryInfo.getId());
        assertThat(updatedDeliveryInfo.getName()).isEqualTo("김선우");
        assertThat(updatedDeliveryInfo.getNickname()).isEqualTo("친구");
        assertThat(updatedDeliveryInfo.getPhone()).isEqualTo("010-3333-4444");
        assertThat(updatedDeliveryInfo.getAddress().getZipcode()).isEqualTo("11111");
        assertThat(updatedDeliveryInfo.getAddress().getStreet()).isEqualTo("학교뒤 1번길");
        assertThat(updatedDeliveryInfo.getAddress().getDetail()).isEqualTo("어딘가");
        assertThat(updatedDeliveryInfo.getIsDefault()).isEqualTo(false);

        //when

        updateDeliveryInfoForm = new UpdateDeliveryInfoForm();
        updateDeliveryInfoForm.setId(deliveryInfo.getId());
        updateDeliveryInfoForm.setName("김선우");
        updateDeliveryInfoForm.setNickname("친구");
        updateDeliveryInfoForm.setPhone("010-3333-4444");
        updateDeliveryInfoForm.setZipcode("11111");
        updateDeliveryInfoForm.setStreet("학교뒤 1번길");
        updateDeliveryInfoForm.setDetail("어딘가");
        updateDeliveryInfoForm.setIsDefault(true); // 배송지 -> 기본 배송지


        memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm);
        findMember = memberService.findUser(memberDto.getLoginId());

        //then

        updatedDeliveryInfo = memberRepository.findDefaultDeliveryInfo(findMember.getLoginId()).orElse(null);
        assertThat(updatedDeliveryInfo.getId()).isEqualTo(deliveryInfo.getId());
        assertThat(updatedDeliveryInfo.getName()).isEqualTo("김선우");
        assertThat(updatedDeliveryInfo.getNickname()).isEqualTo("친구");
        assertThat(updatedDeliveryInfo.getPhone()).isEqualTo("010-3333-4444");
        assertThat(updatedDeliveryInfo.getAddress().getZipcode()).isEqualTo("11111");
        assertThat(updatedDeliveryInfo.getAddress().getStreet()).isEqualTo("학교뒤 1번길");
        assertThat(updatedDeliveryInfo.getAddress().getDetail()).isEqualTo("어딘가");
        assertThat(updatedDeliveryInfo.getIsDefault()).isEqualTo(true);

    }

    // 배송지 수정 실패 -> 배송지 못찾음
    @Test
    public void 배송지_수정_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        // when

        Member findMember = memberService.findUser(memberDto.getLoginId());

        UpdateDeliveryInfoForm updateDeliveryInfoForm = new UpdateDeliveryInfoForm();
        updateDeliveryInfoForm.setId(-1L);
        updateDeliveryInfoForm.setName("김선우");
        updateDeliveryInfoForm.setNickname("친구");
        updateDeliveryInfoForm.setPhone("010-3333-4444");
        updateDeliveryInfoForm.setZipcode("11111");
        updateDeliveryInfoForm.setStreet("학교뒤 1번길");
        updateDeliveryInfoForm.setDetail("어딘가");
        updateDeliveryInfoForm.setIsDefault(false);

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm)); // 배송지 수정
        assertThat(e.getMessage()).isEqualTo("배송지 수정에 문제가 생겼습니다.");

    }

    @Test
    public void 배송지_삭제_성공() {
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        DeliveryInfoForm deliveryInfoForm = new DeliveryInfoForm();
        deliveryInfoForm.setName("지창민");
        deliveryInfoForm.setNickname("학교");
        deliveryInfoForm.setPhone("010-1111-2222");
        deliveryInfoForm.setZipcode("11111");
        deliveryInfoForm.setStreet("학교앞 1번길");
        deliveryInfoForm.setDetail("어딘가");
        deliveryInfoForm.setIsDefault(false);
        memberService.addDeliveryInfo(memberDto.getLoginId(), deliveryInfoForm);

        Member findMember = memberService.findUser(memberDto.getLoginId());

        // when
        long deliveryInfoId = findMember.getDeliveryInfos().get(1).getId();
        memberService.deleteDeliveryInfo(findMember.getLoginId(), deliveryInfoId);
        findMember = memberService.findUser(findMember.getLoginId());

        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> memberRepository.findDeliveryinfo(memberDto.getLoginId(), deliveryInfoId).get());

        // then
        assertThat(findMember.getDeliveryInfos().size()).isEqualTo(1);
        assertThat(e.getMessage()).isEqualTo("No value present");

    }

    @Test
    public void 배송지_삭제_실패() {
        // given
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        Member findMember = memberService.findUser(memberDto.getLoginId());

        // when
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> memberService.deleteDeliveryInfo(findMember.getLoginId(), -1L));

        // then
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 정보입니다.");

    }

}
