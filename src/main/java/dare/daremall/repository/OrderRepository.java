package dare.daremall.repository;

import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findByLoginId(String loginId) {
        return em.createQuery("select o from Order o" +
                " where o.member.loginId = :loginId" +
                        " order by o.orderDate DESC", Order.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public void remove(Order order) {
        em.remove(order);
    }

    public List<Order> findByItemId(Long itemId) {
        return em.createQuery("select o from Order o, OrderItem oi " +
                " where oi.order = o" +
                " and oi.item.id = :itemId", Order.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    // order 상태가 ORDER인 주문 상품 삭제
    public void removeOrderItem(Long itemId) {
        em.createQuery("delete from OrderItem oi" +
                " where oi.item in (select i from Item i where i.id = :itemId)" +
                " and oi.order in (select o from Order o where o.status = 'ORDER')")
                .setParameter("itemId", itemId)
                .executeUpdate();
    }
}
