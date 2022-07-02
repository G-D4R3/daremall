package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.domain.LikeItem;
import dare.daremall.domain.Member;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class LikeItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired MemberService memberService;

    @Test
    public void 좋아요() {
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberService.join(memberDto);

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("FOR_SALE");
        long bookId = itemService.saveItem(book);

        Item findItem = itemService.findOne(bookId);

        memberService.changeLikeItem(memberDto.getLoginId(), bookId);

        Member findMember = memberService.findUser(memberDto.getLoginId());

        assertThat(findMember.getLikes().size()).isEqualTo(1);
        assertThat(findMember.getLikes().stream().map(li->li.getItem()).collect(Collectors.toList()).contains(findItem)).isTrue();

    }

    @Test
    public void 좋아요_취소() {
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

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("FOR_SALE");
        long bookId = itemService.saveItem(book);

        Item findItem = itemService.findOne(bookId);
        memberService.changeLikeItem(memberDto.getLoginId(), bookId); // 좋아요 추가
        Member findMember = memberService.findUser(memberDto.getLoginId());

        // when
        memberService.changeLikeItem(memberDto.getLoginId(), bookId); // 좋아요 취소

        // then
        assertThat(findMember.getLikes().size()).isEqualTo(0);
        assertThat(findMember.getLikes().stream().map(li->li.getItem()).collect(Collectors.toList()).contains(findItem)).isFalse();

    }
}