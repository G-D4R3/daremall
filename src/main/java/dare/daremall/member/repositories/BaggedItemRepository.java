package dare.daremall.member.repositories;

import dare.daremall.member.domains.BaggedItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BaggedItemRepository {

    private final EntityManager em;

    public Optional<BaggedItem> findById(Long baggedItemId) {
        return Optional.ofNullable(em.find(BaggedItem.class, baggedItemId));
    }

    public void remove(Long id) {
        em.createQuery("delete from BaggedItem bi" +
                " where bi.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * 주문
     * @param loginId
     * @return
     */

    public List<BaggedItem> findSelected(String loginId) {
        return em.createQuery("select bi from BaggedItem bi" +
                        " join fetch bi.member" +
                        " join fetch bi.item" +
                " where bi.checked = true" +
                " and bi.member.loginId = :loginId", BaggedItem.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public void removeSelected(String loginId) {
        em.createQuery("delete from BaggedItem bi"+
                " where bi.checked = true" +
                " and bi.member in (select m from Member m where m.loginId = :loginId)")
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
