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

    public BaggedItem findById(Long baggedItemId) {
        return em.find(BaggedItem.class, baggedItemId);
    }

    public void remove(Long id) {
        em.remove(em.find(BaggedItem.class, id));
    }

    /**
     * 주문
     * @param loginId
     * @return
     */

    public List<BaggedItem> findSelected(String loginId) {
        return em.createQuery("select bi from BaggedItem bi" +
                        " join fetch bi.member" +
                " where bi.checked = true" +
                " and bi.member.loginId = :loginId", BaggedItem.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public void removeSelected(String loginId) {
        em.createQuery("delete from BaggedItem bi" +
                " where bi.checked = true" +
                " and bi.member.loginId = :loginId")
                .setParameter("loginId", loginId)
                .executeUpdate();
    }

    public List<BaggedItem> findByMember(String loginId) {
        return em.createQuery("select bi from BaggedItem bi" +
                        " join fetch bi.member" +
                " where bi.member.loginId = :loginId", BaggedItem.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    // 선택한 item이 있을 때 전체 주문을 위함
    // 이후에 item check는 결제할 당시에만 사용하고, all checked로 사용하기
    public void setAllChecked(String loginId) {
        em.createQuery("update BaggedItem bi set bi.checked = TRUE" +
                " where bi.member in (select m from Member m where m.loginId = :loginId)")
                .setParameter("loginId", loginId)
                .executeUpdate();
    }

    public void removeByItemId(Long itemId) {
        em.createQuery("delete from BaggedItem bi where bi.item.id =: itemId").setParameter("itemId", itemId).executeUpdate();
    }

    public void updateCount(Long itemId, int stockQuantity) {
        em.createQuery("update BaggedItem bi set bi.count = :stockQuantity where bi.count > :stockQuantity and bi.item.id = :itemId")
                .setParameter("stockQuantity", stockQuantity)
                .setParameter("itemId", itemId)
                .executeUpdate();
    }


    /** **/
}
