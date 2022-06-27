package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.domain.LikeItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class LikeItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired MemberService memberService;

    /*@Test
    void 좋아요() {
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

        Item findItem = itemService.findOne(book.getId());

        memberService.changeLikeItem(memberDto.getLoginId(), findItem.getId());

        Member findMember = memberService.findUser(memberDto.getLoginId());

        assertThat(findMember.getLikes().size()).isEqualTo(1);
        assertThat(findMember.getLikes().get(0).getItem()).isEqualTo(findItem);

    }

    @Test
    void 좋아요_취소() {
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

        Item findItem = itemService.findOne(book.getId());
        memberService.changeLikeItem(memberDto.getLoginId(), findItem.getId());

        Member findMember = memberService.findUser(memberDto.getLoginId());

        assertThat(findMember.getLikes().size()).isEqualTo(1);
        assertThat(findMember.getLikes().get(0).getItem()).isEqualTo(findItem);

        // when
        memberService.changeLikeItem(findMember.getLoginId(), findItem.getId());
        findMember = memberService.findUser(findMember.getLoginId());

        // then
        assertThat(findMember.getLikes().size()).isEqualTo(0);

    }*/
}