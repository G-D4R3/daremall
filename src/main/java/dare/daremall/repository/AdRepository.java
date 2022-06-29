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
        return em.createQuery("select a from Ad a order by a.status desc", Ad.class).getResultList();
    }

    public void remove(Long adId) {
        em.remove(em.find(Ad.class, adId));
    }

    /**
     * 관리자 페이지 - ad
     * @return
     */
    public List<MainAd> findMainAd() {
        return em.createQuery("select ma from MainAd ma" +
                " order by ma.status", MainAd.class).getResultList();
    }

    public List<Ad> findByName(String search) {
        return em.createQuery("select a from Ad a" +
                        " where upper(a.name) like upper(:name)" +
                        " order by a.status", Ad.class)
                .setParameter("name", "%"+search+"%")
                .getResultList();
    }
    /** **/


    /**
     * @return 메인 페이지에 표시될
     */
    public List<MainAd> findMainAdNow() {
        em.createQuery("update MainAd ma set ma.status = 'END' where ma.endDate <= :today")
                    .setParameter("today", LocalDate.now()).executeUpdate();
        return em.createQuery("select ma from MainAd ma" +
                " where ma.startDate <= :date" +
                " and ma.endDate >= :date" +
                " and ma.status = 'NOW'" +
                        " order by ma.status", MainAd.class)
                .setParameter("date", LocalDate.now())
                .getResultList();
    }

}
