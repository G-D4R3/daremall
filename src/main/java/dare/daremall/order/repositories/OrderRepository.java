package dare.daremall.order.repositories;

import dare.daremall.order.domains.Order;
import dare.daremall.order.domains.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        if(order.getId()==null) {
            em.persist(order);
        }
        else em.merge(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findByLoginId(String loginId) {
        return em.createQuery("select o from Order o" +
                        " join fetch o.member" +
                        " join fetch o.orderItems" +
                        " where o.member.loginId = :loginId" +
                        " order by o.orderDate DESC", Order.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public Optional<Order> findOrder(Long orderId, String loginId) {
        return em.createQuery("select o from Order o" +
                        " join fetch o.orderItems" +
                        " join fetch o.delivery" +
                " where o.member.loginId = :loginId" +
                " and o.id = :orderId", Order.class)
                .setParameter("loginId", loginId)
                .setParameter("orderId", orderId)
                .getResultList().stream().findAny();
    }

    public List<Order> findAll() {
        return em.createQuery("select o from Order o order by o.id desc", Order.class).getResultList();
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
                " and oi.item.id = :itemId" +
                        " and o.status = 'ORDER'", Order.class)
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
                            " order by o.id desc", Order.class)
                    .setParameter("orderId", Long.parseLong(search))
                    .setParameter("search", "%"+search+"%")
                    .getResultList();
        }
        else {
            return em.createQuery("select o from Order o " +
                            " where upper(o.member.loginId) like upper(:search)" +
                            " or upper(o.member.name) like upper(:search)" +
                            " order by o.id desc", Order.class)
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

    public Optional<OrderItem> findOrderItem(Long orderId, Long itemId) {
        return em.createQuery("select oi from OrderItem oi" +
                " where oi.item.id = :itemId" +
                " and oi.order.id = :orderId", OrderItem.class)
                .setParameter("itemId", itemId)
                .setParameter("orderId", orderId)
                .getResultList().stream().findAny();
    }

    public List<Order> findKakaoPay(String loginId) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member" +
                " where o.member.loginId = :loginId" +
                " and o.paymentType = 'KAKAO'", Order.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }


    /** **/

}
