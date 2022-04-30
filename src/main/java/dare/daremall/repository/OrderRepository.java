package dare.daremall.repository;

import dare.daremall.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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
}
