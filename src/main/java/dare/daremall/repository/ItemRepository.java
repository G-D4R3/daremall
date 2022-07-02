package dare.daremall.repository;

import dare.daremall.domain.LikeItem;
import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.domain.item.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if( item.getId() == null ) { // id가 없다. 새로 생성하는 객체
            em.persist(item);
        }
        else {
            em.merge(item); // update 비슷
            // merge는 영속성으로 관리됨. item은 준영속
        }
    }

    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(em.find(Item.class, itemId));
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i order by i.itemStatus", Item.class).getResultList();
    }

    public void remove(Long itemId) {
        em.remove(em.find(Item.class, itemId));
    }

    /**
     * 사용자 페이지
     */

    /**
     * @return 상품 검색
     */
    public List<Item> findByName(String name) {
        return em.createQuery("select i from Item i" +
                        " where upper(i.name) like upper(:name)" +
                        " order by i.itemStatus", Item.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }

    public List<Item> findExceptHide() {
        return em.createQuery("select i from Item i where i.itemStatus <> 'HIDE'" +
                " order by i.itemStatus", Item.class).getResultList();
    }

    public List findByType(String type) {
        if(type.equals("album")) {
            return em.createQuery("select a from Album a" +
                    " where a.itemStatus <> 'HIDE'"  +
                    " order by a.itemStatus", Album.class).getResultList();
        }
        else if(type.equals("book")) {
            return em.createQuery("select b from Book b" +
                    " where b.itemStatus <> 'HIDE'"  +
                    " order by b.itemStatus", Book.class).getResultList();
        }
        else return em.createQuery("select i from Item i where i.itemStatus <> 'HIDE'" +
                    " order by i.itemStatus", Item.class).getResultList();
    }

    public List<Book> findBookByName(String name) {
        return em.createQuery("select b from Book b" +
                " where upper(b.name) like upper(:name)" +
                " or upper(b.author) like upper(:name)" +
                        " order by b.itemStatus", Book.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }

    public List<Album> findAlbumByName(String name) {
        return em.createQuery("select a from Album a" +
                " where upper(a.name) like upper(:name)" +
                " or upper(a.artist) like upper(:name)" +
                        " order by a.itemStatus", Album.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }
    /** **/


    /**
     * 관리자 페이지 - /item
     * @return status별 조회
     */
    public List<Item> findByStatus(String status) {
        return em.createQuery("select i from Item i" +
                " where i.itemStatus = upper(:status)", Item.class)
                .setParameter("status", status)
                .getResultList();
    }
}
