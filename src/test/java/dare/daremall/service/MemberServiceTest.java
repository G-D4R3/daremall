package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.member.mypage.DeliveryInfoForm;
import dare.daremall.controller.member.mypage.UpdateDeliveryInfoForm;
import dare.daremall.domain.BaggedItem;
import dare.daremall.domain.DeliveryInfo;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
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

    /*@Test
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
        MemberSignupRequestDto memberDto1 = new MemberSignupRequestDto();
        memberDto1.setName("지창민");
        memberDto1.setLoginId("jcm115");
        memberDto1.setPassword("pass1234");
        memberDto1.setPhone("010-1111-2222");
        memberDto1.setZipcode("00000");
        memberDto1.setStreet("강가 1번길");
        memberDto1.setDetail("어딘가");

        MemberSignupRequestDto memberDto2 = new MemberSignupRequestDto();
        memberDto2.setName("지창민");
        memberDto2.setLoginId("jcm115");
        memberDto2.setPassword("pass1234");
        memberDto2.setPhone("010-1111-2222");
        memberDto2.setZipcode("00000");
        memberDto2.setStreet("강가 1번길");
        memberDto2.setDetail("어딘가");

        // when
        memberService.join(memberDto1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(memberDto2));

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

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(memberDto4));

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

        // then
        assertThat(memberService.findUser(memberDto.getLoginId()).getLoginId()).isEqualTo(memberDto.getLoginId());
        assertThat(memberService.findUser("notMember")).isEqualTo(null);
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

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        itemService.saveItem(book);

        // when
        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(book.getId(), findMember.getLoginId(), 2);

        // then
        assertThat(findMember.getShoppingBag().get(0).getItem()).isEqualTo(book);
        assertThat(findMember.getShoppingBag().get(0).getCount()).isEqualTo(2);
        assertThat(findMember.getShoppingBag().stream().mapToInt(bi->bi.getTotalPrice()).sum()).isEqualTo(2000);
        assertThat(book.getStockQuantity()).isEqualTo(10);

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

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        itemService.saveItem(book);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(book.getId(), findMember.getLoginId(), 2);

        // when
        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);
        memberService.removeShoppingBag(findMember.getLoginId(), baggedItem.getId());

        // then
        assertThat(findMember.getShoppingBag().size()).isEqualTo(0);

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

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        itemService.saveItem(book);

        Member findMember = memberService.findUser(memberDto.getLoginId());
        memberService.addShoppingBag(book.getId(), findMember.getLoginId(), 2);

        // when
        findMember = memberService.findUser(memberDto.getLoginId()); // em.merge로 save했기 때문에 영속성 관리
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);
        memberService.updateBaggedItemCount(baggedItem.getId(), 5);

        // then
        assertThat(baggedItemRepository.findById(baggedItem.getId()).getCount()).isEqualTo(5);

    }

    @Test
    public void 주소지_변경() {
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

    }

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

        // then
        Member findMember = memberService.findUser(memberDto.getLoginId());
        assertThat(findMember.getDeliveryInfos().size()).isEqualTo(2);

    }

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
        assertThat(updatedDefaultDeliveryInfo.getIsDefault()).isEqualTo(true);

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

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm));

        // then
        assertThat(e.getMessage()).isEqualTo("기본 배송지는 삭제할 수 없습니다.");

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
        memberService.addDeliveryInfo(memberDto.getLoginId(), deliveryInfoForm);

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

        memberService.updateDeliveryInfo(findMember.getLoginId(), updateDeliveryInfoForm);
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
        updateDeliveryInfoForm.setIsDefault(true);


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
        DeliveryInfo deliveryInfo = findMember.getDeliveryInfos().get(1);
        assertThat(deliveryInfo.getNickname()).isEqualTo("학교");
        memberService.deleteDeliveryInfo(findMember.getLoginId(), deliveryInfo.getId());

        // then
        findMember = memberService.findUser(findMember.getLoginId());
        assertThat(findMember.getDeliveryInfos().size()).isEqualTo(1);

    }

    @Test
    public void 장바구니_상품_수량_초과() {
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

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        itemService.saveItem(book);

        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 2);
        Member findMember = memberService.findUser(memberDto.getLoginId());

        // when
        BaggedItem baggedItem = findMember.getShoppingBag().get(0);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.updateBaggedItemCount(baggedItem.getId(), 11));

        // then
        assertThat(e.getMessage()).isEqualTo("재고 수량을 초과했습니다.");

    }*/

}
