package dare.daremall.repository;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
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
            Item merge = em.merge(item); // update 비슷
            // merge는 영속성으로 관리됨. item은 준영속
        }
    }

    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(em.find(Item.class, itemId));
    }

    public List<Item> findByName(String name) {
        return em.createQuery("select i from Item i" +
                        " where i.name like upper(:name)", Item.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

    public List findByType(String type) {
        if(type.equals("album")) {
            return em.createQuery("select a from Album a", Album.class).getResultList();
        }
        else if(type.equals("book")) {
            return em.createQuery("select b from Book b", Book.class).getResultList();
        }
        else return em.createQuery("select i from Item i", Item.class).getResultList();
    }

    public List<Book> findBookByName(String name) {
        return em.createQuery("select b from Book b" +
                " where b.name like upper(:name)" +
                " or b.author like upper(:name)", Book.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }

    public List<Album> findAlbumByName(String name) {
        return em.createQuery("select a from Album a" +
                " where a.name like upper(:name)" +
                " or a.artist like upper(:name)", Album.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
    }
}
