package dare.daremall.member.repositories;

import dare.daremall.member.domains.DeliveryInfo;
import dare.daremall.member.domains.LikeItem;
import dare.daremall.member.domains.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member) {
        if(member.getId() == null) {
            em.persist(member);
            return member;
        }
        else {
            return em.merge(member);
        }
    }

    public Optional<Member> findById(Long id) {
        Optional<Member> member = Optional.ofNullable(em.find(Member.class, id));
        if(member==null) return Optional.empty();
        else return member;
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findByLoginId(String loginId) {
        return em.createQuery("select m from Member m" +
                " where m.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    /**
     * 회원가입시 같은 이름, 휴대폰 번호 조합으로 가입된 계정 조회
     * @param name
     * @param phone
     * @return
     */
    public List<Member> findLoginIdByNameAndPhone(String name, String phone) {
        return em.createQuery("select m from Member m" +
                        " where m.name = :name" +
                        " and m.phone = :phone", Member.class)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .getResultList();
    }

    /**
     * 비밀번호 찾기 시 사용자 조회
     * @param loginId
     * @param phone
     * @return
     */
    public Optional<Member> findMemberByLoginId(String loginId, String phone) {
        return em.createQuery("select m from Member m" +
                " where m.loginId = :loginId" +
                " and m.phone = :phone", Member.class)
                .setParameter("loginId", loginId)
                .setParameter("phone", phone)
                .getResultList().stream().findAny();
    }

    public Optional<DeliveryInfo> findDeliveryInfo(String loginId, Long deliveryId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.id = :deliveryId" +
                " and di.member.loginId = :loginId", DeliveryInfo.class)
                .setParameter("deliveryId", deliveryId)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    /**
     * 회원 정보 관리 페이지
     */

    /**
     * 배송지 조회
     * @param loginId
     * @return
     */
    public List<DeliveryInfo> findDeliveryInfos(String loginId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.member.loginId = :loginId" +
                " order by di.isDefault desc, di.id", DeliveryInfo.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    /**
     * 기본 배송지 조회
     * @param loginId
     * @return
     */
    public Optional<DeliveryInfo> findDefaultDeliveryInfo(String loginId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.member.loginId = :loginId" +
                " and di.isDefault = true", DeliveryInfo.class)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    /**
     * 배송지 삭제
     * @param deliveryId
     */
    public void removeDeliveryInfo(Long deliveryId) {
        em.createQuery("delete from DeliveryInfo di where di.id = :deliveryId")
                .setParameter("deliveryId", deliveryId)
                .executeUpdate();
    }

    /**
     * 찜한 상품 조회
     */
    public List<LikeItem> getLikes(String loginId) {
        return em.createQuery("select li from LikeItem li" +
                " join fetch li.member" +
                " where li.member.loginId = :loginId")
                .setParameter("loginId", loginId)
                .getResultList();
    }



    /** **/


    /**
     * 관리자 페이지 - /member
     */

    /**
     * 관리자 페이지 회원 이름, 아이디로 조회
     * @param search
     * @return
     */
    public List<Member> findMembersByString(String search) {
        return em.createQuery("select m from Member m" +
                " where upper(m.name) like upper(:search)" +
                " or upper(m.loginId) like upper(:search)", Member.class)
                .setParameter("search", "%"+search+"%")
                .getResultList();
    }

    /**
     * 관리자 페이지
     * @return 관리자 계정
     */
    public List<Member> findAdmins() {
        return em.createQuery("select m from Member m" +
                " where m.role = 'ROLE_ADMIN'", Member.class).getResultList();
    }

    public void remove(long memberId) {
        em.remove(em.find(Member.class, memberId));
    }

    public void removeBaggedItemByItemId(Long itemId) {
        em.createQuery("delete from BaggedItem bi" +
                " where bi.item.id = :itemId")
                .setParameter("itemId", itemId)
                .executeUpdate();
    }


    /** **/
}
