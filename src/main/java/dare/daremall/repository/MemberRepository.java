package dare.daremall.repository;

import dare.daremall.domain.DeliveryInfo;
import dare.daremall.domain.Member;
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

    public List<Member> findLoginIdByName(String name, String phone) {
        return em.createQuery("select m from Member m" +
                        " where m.name = :name" +
                        " and m.phone = :phone", Member.class)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .getResultList();
    }

    public Optional<Member> findMemberByLoginId(String loginId, String phone) {
        return em.createQuery("select m from Member m" +
                " where m.loginId = :loginId" +
                " and m.phone = :phone", Member.class)
                .setParameter("loginId", loginId)
                .setParameter("phone", phone)
                .getResultList().stream().findAny();
    }

    public Optional<DeliveryInfo> findDeliveryinfo(String loginId, Long deliveryId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.id = :deliveryId" +
                " and di.member.loginId = :loginId", DeliveryInfo.class)
                .setParameter("deliveryId", deliveryId)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    public List<DeliveryInfo> findDeliveryInfos(String loginId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.member.loginId = :loginId" +
                " order by di.isDefault desc, di.id", DeliveryInfo.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public Optional<DeliveryInfo> findDefaultDeliveryInfo(String loginId) {
        return em.createQuery("select di from DeliveryInfo di" +
                " where di.member.loginId = :loginId" +
                " and di.isDefault = true", DeliveryInfo.class)
                .setParameter("loginId", loginId)
                .getResultList().stream().findAny();
    }

    public List<Member> findMembers(String search) {
        return em.createQuery("select m from Member m" +
                " where upper(m.name) like upper(:search)" +
                " or upper(m.loginId) like upper(:search)", Member.class)
                .setParameter("search", "%"+search+"%")
                .getResultList();
    }

    public List<Member> findAdmins() {
        return em.createQuery("select m from Member m" +
                " where m.role = 'ROLE_ADMIN'", Member.class).getResultList();
    }
}
