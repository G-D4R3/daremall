package dare.daremall.repository;

import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Arrays;
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

    public List<Order> findAll() {
        return em.createQuery("select o from Order o order by o.id, o.status", Order.class).getResultList();
    }

    public void remove(Order order) {
        em.remove(order);
    }

    /**
     * 해당 상품을 주문한 주문 조회
     * @param itemId
     * @return
     */
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

    /** **/


    /**
     * 관리자 페이지 - /order
     */


    public List<Order> findByName(String search) {
        if(search.matches("[0-9]+")) {
            return em.createQuery("select o from Order o " +
                            " where o.id = :orderId" +
                            " or upper(o.member.loginId) like upper(:search)" +
                            " order by o.id, o.status", Order.class)
                    .setParameter("orderId", Long.parseLong(search))
                    .setParameter("search", "%"+search+"%")
                    .getResultList();
        }
        else {
            return em.createQuery("select o from Order o " +
                            " where upper(o.member.loginId) like upper(:search)" +
                            " or upper(o.member.name) like upper(:search)" +
                            " order by o.id, o.status", Order.class)
                    .setParameter("search", "%"+search+"%")
                    .getResultList();
        }
    }

    public List<Order> findNow() {
        return em.createQuery("select o from Order o" +
                " where (o.status = 'ORDER'" +
                " or o.status = 'PAY')" +
                " and o.delivery.status <> 'COMP'", Order.class)
                .getResultList();
    }

    public List<Order> findComp() {
        return em.createQuery("select o from Order o" +
                " where o.delivery.status = 'COMP'", Order.class)
                .getResultList();
    }

    public List<Order> findCancel() {
        return em.createQuery("select o From Order o" +
                " where o.status = 'CANCEL'", Order.class)
                .getResultList();
    }

    /** **/
}
