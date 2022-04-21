package dare.daremall.repository;

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

    public void save(Member member) {
        if(member.getId() == null) {
            em.persist(member);
        }
        else {
            em.merge(member);
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

}
