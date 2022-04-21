package dare.daremall.repository;

import dare.daremall.domain.LikeItem;
import dare.daremall.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class LikeItemRepository {

    private final EntityManager em;

    public void save (LikeItem likeItem) {
        em.persist(likeItem);
    }

    public Optional<LikeItem> findById(Long likeItemId) {
        Optional<LikeItem> likeItem = Optional.ofNullable(em.find(LikeItem.class, likeItemId));
        return likeItem;
    }

    public List<LikeItem> findByMemberId(Long memberId) {
        return em.createQuery("select li from LikeItem li" +
                " where li.member.id = :memberId", LikeItem.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<LikeItem> findByIds(Long memberId, Long itemId) {
        return em.createQuery("select li from LikeItem li" +
                        " where li.member.id = :memberId" +
                        " and li.item.id = :itemId", LikeItem.class)
                .setParameter("memberId", memberId)
                .setParameter("itemId", itemId)
                .getResultList().stream().findAny();
    }

    public void remove(Long likeItemId) {
        em.remove(em.find(LikeItem.class, likeItemId));
    }
}
