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

    public LikeItem findById(Long likeItemId) {
        return em.find(LikeItem.class, likeItemId);
    }

    public void remove(Long likeItemId) {
        em.remove(em.find(LikeItem.class, likeItemId));
    }


    /**
     * 사용자 페이지
     * @param memberId
     * @return 사용자가 좋아요 표시한 상품
     */
    public List<LikeItem> findByMemberId(Long memberId) {
        return em.createQuery("select li from LikeItem li" +
                " where li.member.id = :memberId", LikeItem.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 좋아요 표시를 위한 상품 조회
    public Optional<LikeItem> findByIds(String loginId, Long itemId) {
        return em.createQuery("select li from LikeItem li" +
                        " where li.member.loginId = :loginId" +
                        " and li.item.id = :itemId", LikeItem.class)
                .setParameter("loginId", loginId)
                .setParameter("itemId", itemId)
                .getResultList().stream().findAny();
    }
    /** **/
}
