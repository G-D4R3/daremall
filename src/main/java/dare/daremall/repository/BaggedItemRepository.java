package dare.daremall.repository;

import dare.daremall.domain.BaggedItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

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
}
