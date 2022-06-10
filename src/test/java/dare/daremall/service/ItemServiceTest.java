package dare.daremall.service;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;

    @Test
    void 상품_추가() {

        // given
        Album album = new Album();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setArtist("artist1");
        album.setEtc("etc1");
        itemService.saveItem(album);

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");

        // when
        itemService.saveItem(book);

        // then
        assertThat(itemService.findOne(album.getId()).getId()).isEqualTo(album.getId());
        assertThat(itemService.findOne(book.getId()).getId()).isEqualTo(book.getId());
    }

    @Test
    void 상품_수정() {

        // given
        Album album = new Album();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setArtist("artist1");
        album.setEtc("etc1");
        itemService.saveItem(album);

        // when
        //itemService.updateItem(album.getId(), "album2", 2000, 5);
        Item findItem = itemService.findOne(album.getId());

        // then
        assertThat(findItem.getName()).isEqualTo("album2");
        assertThat(findItem.getPrice()).isEqualTo(2000);
        assertThat(findItem.getStockQuantity()).isEqualTo(5);

    }

    @Test
    void 상품_조회() {
        Album album = new Album();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setArtist("artist1");
        album.setEtc("etc1");
        itemService.saveItem(album);

        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        itemService.saveItem(book);

        assertThat(itemService.findItems().size()).isEqualTo(15+2);
    }

    @Test
    void 상품명으로_조회() {

        // given
        Book book = new Book();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");

        // when
        itemService.saveItem(book);

        // then
        assertThat(itemService.findByName("book").get(0).getName()).isEqualTo("book1");
    }
}