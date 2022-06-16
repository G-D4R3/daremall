package dare.daremall.repository;

import dare.daremall.domain.Order;
import dare.daremall.domain.statistics.OrderStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderStatisticsRepository {

    private final EntityManager em;

    public void save(OrderStatistics orderStatistics) {
        if(orderStatistics.getId() == null) {
            em.persist(orderStatistics);
        }
        else {
            em.merge(orderStatistics);
        }
    }

    public Optional<OrderStatistics> findOne(LocalDate date) {
        return em.createQuery("select os from OrderStatistics os" +
                " where os.date= :date", OrderStatistics.class)
                .setParameter("date", date)
                .getResultList().stream().findAny();
    }

    public List<OrderStatistics> findAll() {
        return em.createQuery("select os from OrderStatistics os", OrderStatistics.class).getResultList();
    }
}
