package dare.daremall.repository;

import dare.daremall.domain.BaggedItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BaggedItemRepository {

    private final EntityManager em;

    public void remove(Long id) {
        em.remove(em.find(BaggedItem.class, id));
    }

    public BaggedItem findById(Long baggedItemId) {
        return em.find(BaggedItem.class, baggedItemId);
    }

    public List<BaggedItem> findSelected(String loginId) {
        return em.createQuery("select bi from BaggedItem bi" +
                " where bi.checked = true" +
                " and bi.member.loginId = :loginId", BaggedItem.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public List<BaggedItem> findByMember(String loginId) {
        return em.createQuery("select bi from BaggedItem bi" +
                " where bi.member.loginId = :loginId", BaggedItem.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }
}
