package dare.daremall.service;

import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.order.OrderForm;
import dare.daremall.domain.DeliveryStatus;
import dare.daremall.domain.Member;
import dare.daremall.domain.Order;
import dare.daremall.domain.OrderStatus;
import dare.daremall.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class OrderServiceTest {

    @Autowired MemberService memberService;
    @Autowired ItemService itemService;
    @Autowired OrderService orderService;

    @Test
    public void 주문_성공() {
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
        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        Long orderId = orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY);

        // then
        Member findMember = memberService.findUser(memberDto.getLoginId());
        Order order = orderService.findOne(orderId);

        assertThat(order.getMember()).isEqualTo(findMember);
        assertThat(order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isEqualTo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertThat(order.getTotalPrice()).isEqualTo(3000+2500);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getOrderItems().get(0).getItem()).isEqualTo(book);
        assertThat(order.getOrderItems().get(0).getCount()).isEqualTo(3);

    }

    @Test
    public void 주문_수량_초과() {
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
        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 14);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY));

        // then
        assertThat(e.getMessage()).isEqualTo("주문 수량을 초과하였습니다.");
    }

    @Test
    public void 주문_취소() {
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

        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        Long orderId = orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY);

        // when
        orderService.cancelOrder(orderId);

        // then
        assertThat(orderService.findOne(orderId).getStatus()).isEqualTo(OrderStatus.CANCEL);

    }

    @Test
    public void 주문_취소_실패() {
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

        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        Long orderId = orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY);
        Order order = orderService.findOne(orderId);
        order.getDelivery().setStatus(DeliveryStatus.COMP);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 배송완료된 상품은 취소가 불가능합니다.");

    }

    @Test
    public void 주문_삭제() {
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

        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        Long orderId = orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY);
        orderService.cancelOrder(orderId);

        // when
        orderService.deleteOrder(orderId);

        // then
        assertThat(memberService.findUser(memberDto.getLoginId()).getOrders().size()).isEqualTo(0);
        assertThat(orderService.findOne(orderId)).isEqualTo(null);

    }

    @Test
    public void 주문_삭제_실패() {
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

        memberService.addShoppingBag(book.getId(), memberDto.getLoginId(), 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");

        Long orderId = orderService.createOrder(memberDto.getLoginId(), orderForm, OrderStatus.PAY);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(orderId));

        // then
        assertThat(e.getMessage()).isEqualTo("취소하지 않은 주문은 삭제할 수 없습니다.");

    }
}