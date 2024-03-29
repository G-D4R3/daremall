package dare.daremall.item.repositories;

import dare.daremall.item.domains.Album;
import dare.daremall.item.domains.Book;
import dare.daremall.item.domains.Item;
import dare.daremall.item.domains.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemJpaRepository extends PagingAndSortingRepository<Item, Long> {

    @Query("select i from Item i where i.itemStatus <> ?1")
    Page<Item> findByItemStatus(ItemStatus itemStatus, Pageable pageable);

    @Query("select a from Album a where a.itemStatus <> ?1")
    Page<Album> findAlbumByItemStatus(ItemStatus itemStatus, Pageable pageable);

    @Query("select b from Book b where b.itemStatus <> ?1")
    Page<Book> findBookByItemStatus(ItemStatus itemStatus, Pageable pageable);

    @Query("select i from Item i where i.itemStatus <> 'HIDE' and upper(i.name) like upper(?1)")
    Page<Item> findItemsByName(String name, Pageable pageable);

    @Query("select a from Album a where a.itemStatus <> 'HIDE' and upper(a.name) like upper(?1)")
    Page<Album> findAlbumsByName(String name, Pageable pageable);

    @Query("select b from Book b where b.itemStatus <> 'HIDE' and upper(b.name) like upper(?1)")
    Page<Book> findBooksByName(String name, Pageable pageable);
}
