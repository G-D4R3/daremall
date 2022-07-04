package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.controller.member.auth.MemberSignupRequestDto;
import dare.daremall.controller.order.OrderForm;
import dare.daremall.domain.DeliveryStatus;
import dare.daremall.domain.Member;
import dare.daremall.domain.Order;
import dare.daremall.domain.OrderStatus;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.domain.item.ItemStatus;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.MemberRepository;
import dare.daremall.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class OrderServiceTest {

    @Autowired MemberService memberService;
    @Autowired ItemService itemService;
    @Autowired OrderService orderService;

    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired OrderRepository orderRepository;

    long memberId;
    long albumId, bookId;
    static final String loginId = "jcm115";

    @BeforeEach
    void createMember() {
        MemberSignupRequestDto memberDto = new MemberSignupRequestDto();
        memberDto.setName("지창민");
        memberDto.setLoginId("jcm115");
        memberDto.setPassword("pass1234");
        memberDto.setPhone("010-1111-2222");
        memberDto.setZipcode("00000");
        memberDto.setStreet("강가 1번길");
        memberDto.setDetail("어딘가");
        memberId = memberService.join(memberDto);

        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        albumId = itemService.saveItem(album);

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("NOT_FOR_SALE");

        bookId = itemService.saveItem(book);
    }

    @AfterEach
    void removeMember() {
        memberRepository.remove(memberId);
        itemService.delete(albumId);
        itemService.delete(bookId);
    }

    @Test
    public void 주문_성공() {

        // when
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);

        // then
        Member findMember = memberService.findUser(loginId);
        Order order = orderService.findOne(orderId);
        Item findItem = itemService.findOne(albumId);

        assertThat(order.getMember()).isEqualTo(findMember);
        assertThat(order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isEqualTo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertThat(order.getTotalPrice()).isEqualTo(3000+2500);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getOrderItems().stream().map(oi->oi.getItem()).collect(Collectors.toList()).contains(findItem)).isTrue();
        assertThat(order.getOrderItems().get(0).getCount()).isEqualTo(3);

    }

    // 주문 실패 -> 주문 수량 초과, 판매 중단 상품,
    // controller validation check

    @Test
    public void 주문_실패() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");

        Item findItem = itemService.findOne(albumId);
        findItem.setItemStatus(ItemStatus.NOT_FOR_SALE);
        itemRepository.save(findItem);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null));

        // then
        assertThat(e.getMessage()).isEqualTo("판매 중단된 상품은 주문할 수 없습니다.");

    }

    @Test
    public void 주문_취소() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");

        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);

        // when
        orderService.cancelOrder(orderId, loginId);

        // then
        assertThat(orderService.findOne(orderId).getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(itemService.findOne(albumId).getStockQuantity()).isEqualTo(10);

    }



    // 주문 취소 실패 -> 이미 취소된 주문, 이미 삭제된 주문, 이미 배송 중인 주문

    @Test
    public void 취소된_주문_취소_실패() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);
        orderService.cancelOrder(orderId, loginId);

        // when
        IllegalStateException e1 = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId, loginId));

        // then
        assertThat(e1.getMessage()).isEqualTo("이미 취소된 주문입니다.");

    }

    @Test
    public void 삭제된_주문_취소_실패() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);
        orderService.cancelOrder(orderId, loginId);
        orderService.deleteOrder(orderId, loginId);

        // when
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> orderService.cancelOrder(orderId, loginId));

        // then
        assertThat(e.getMessage()).isEqualTo("일치하는 주문 정보를 찾을 수 없습니다.");
    }

    @Test
    public void 배송중_주문_취소_실패() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);

        Order findOrder = orderService.findOne(orderId);
        findOrder.getDelivery().setStatus(DeliveryStatus.SHIP);
        orderRepository.save(findOrder);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId, loginId));

        // then
        assertThat(e.getMessage()).isEqualTo("배송중인 상품은 취소가 불가능합니다.");

    }


    @Test
    public void 주문_삭제() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);
        orderService.cancelOrder(orderId, loginId);

        // when
        orderService.deleteOrder(orderId, loginId);

        // then
        assertThat(memberService.findUser(loginId).getOrders().size()).isEqualTo(0);
        assertThat(orderService.findOne(orderId)).isEqualTo(null);

    }

    @Test
    public void 주문_삭제_실패() {
        // given
        memberService.addShoppingBag(albumId, loginId, 3);
        OrderForm orderForm = new OrderForm();
        orderForm.setName("지창민");
        orderForm.setPhone("010-1111-2222");
        orderForm.setPayment("pay");
        orderForm.setZipcode("00000");
        orderForm.setStreet("강가 1번길");
        orderForm.setDetail("어딘가");
        orderForm.setPayment("DEPOSIT");
        Long orderId = orderService.createOrder(loginId, orderForm, OrderStatus.ORDER, null, null);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(orderId, loginId));

        // then
        assertThat(e.getMessage()).isEqualTo("취소하지 않은 주문은 삭제할 수 없습니다.");

    }

    /*// 주문 정보 update
    @Test
    public void 주문_정보_수정() {
        // given

        // when

        // then

    }

    // 상품 정보 수정시 order update*/
}