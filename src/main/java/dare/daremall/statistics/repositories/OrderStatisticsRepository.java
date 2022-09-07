package dare.daremall.statistics.repositories;

import dare.daremall.statistics.domains.OrderStatistics;
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

    public List<OrderStatistics> findAllWeek() {
        LocalDate now = LocalDate.now();
        return em.createQuery("select os from OrderStatistics os" +
                " where os.date between :last and :now", OrderStatistics.class)
                .setParameter("last", now.minusDays(6))
                .setParameter("now", now)
                .getResultList();
    }
}
