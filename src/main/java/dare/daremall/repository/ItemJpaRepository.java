package dare.daremall.repository;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.domain.item.ItemStatus;
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
}
