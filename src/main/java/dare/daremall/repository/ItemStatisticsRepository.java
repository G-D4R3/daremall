package dare.daremall.repository;

import dare.daremall.domain.statistics.ItemStatistics;
import dare.daremall.domain.statistics.OrderStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemStatisticsRepository {

    private final EntityManager em;

    public void save(ItemStatistics itemStatistics) {
        if(itemStatistics.getId() == null) {
            em.persist(itemStatistics);
        }
        else {
            em.merge(itemStatistics);
        }
    }

    public List<ItemStatistics> findByDate(LocalDate date) {
        return em.createQuery("select is from ItemStatistics is" +
                        " where is.date = :date", ItemStatistics.class)
                .setParameter("date", date)
                .getResultList();
    }

    public List<ItemStatistics> findByItem(Long itemId) {
        return em.createQuery("select iss from ItemStatistics iss" +
                " where iss.item.id = :itemId", ItemStatistics.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public List<ItemStatistics> findAll() {
        return em.createQuery("select is from ItemStatistics is", ItemStatistics.class).getResultList();
    }

    public Optional<ItemStatistics> findStatistics(Long id, LocalDate orderDate) {
        return em.createQuery("select iss from ItemStatistics iss" +
                " where iss.item.id = :itemId" +
                " and iss.date = :date", ItemStatistics.class)
                .setParameter("itemId", id)
                .setParameter("date", orderDate)
                .getResultList().stream().findAny();
    }
}
