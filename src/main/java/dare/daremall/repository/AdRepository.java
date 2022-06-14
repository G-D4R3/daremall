package dare.daremall.repository;

import dare.daremall.domain.ad.Ad;
import dare.daremall.domain.ad.MainAd;
import lombok.RequiredArgsConstructor;
import org.iq80.snappy.Main;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdRepository {

    private final EntityManager em;

    public void save(Ad ad){
        if(ad.getId() == null) {
            em.persist(ad);
        }
        else {
            em.merge(ad);
        }
    }

    public Ad findOne(Long id) {
        return em.find(Ad.class, id);
    }

    public List<Ad> findAll() {
        return em.createQuery("select a from Ad a", Ad.class).getResultList();
    }

    public List<MainAd> findMainAd() {
        return em.createQuery("select ma from MainAd ma", MainAd.class).getResultList();
    }

    public List<MainAd> findMainAdNow() {
        return em.createQuery("select ma from MainAd ma" +
                " where ma.start <= :date" +
                " and ma.end >= :date" +
                " and ma.status = 'NOW'", MainAd.class)
                .setParameter("date", LocalDate.now())
                .getResultList();
    }

    public void remove(Long adId) {
        em.remove(em.find(Ad.class, adId));
    }

    public List<Ad> findByName(String search) {
        return em.createQuery("select a from Ad a" +
                " where upper(a.name) like upper(:name)", Ad.class)
                .setParameter("name", "%"+search+"%")
                .getResultList();
    }
}
