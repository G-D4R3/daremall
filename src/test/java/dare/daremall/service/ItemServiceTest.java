package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.domain.item.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;

    /** 관리자 기능 **/

    @Test
    void 상품_추가() {

        // given
        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("FOR_SALE");

        // when
        long albumId = itemService.saveItem(album);
        long bookId = itemService.saveItem(book);

        // then
        assertThat(itemService.findOne(albumId).getName()).isEqualTo(album.getName());
        assertThat(itemService.findOne(albumId).getImagePath()).isEqualTo(null);

        assertThat(itemService.findOne(bookId).getName()).isEqualTo(book.getName());
    }

    @Test
    void 상품_수정() {

        // given
       ItemDto album = new ItemDto();
       album.setName("album1");
       album.setPrice(1000);
       album.setStockQuantity(10);
       album.setType("A");
       album.setArtist("artist1");
       album.setEtc("etc1");
       album.setItemStatus("FOR_SALE");

       long albumId = itemService.saveItem(album);

       ItemDto updateAlbum = new ItemDto();
       updateAlbum.setId(albumId);

       /** change **/
       updateAlbum.setName("album2");
       updateAlbum.setPrice(2000);
       updateAlbum.setStockQuantity(5);

       // when
       itemService.updateItem(updateAlbum);
       Album findItem = (Album)itemService.findOne(albumId);

       // then
       assertThat(findItem.getName()).isEqualTo("album2");
       assertThat(findItem.getPrice()).isEqualTo(2000);
       assertThat(findItem.getStockQuantity()).isEqualTo(5);
       assertThat(findItem.getArtist()).isEqualTo("artist1");

    }

    @Test
    public void 상품_삭제() {
        // given
        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        // when
        itemService.delete(albumId);

        // then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> itemService.findOne(albumId));
        assertThat(e.getMessage()).isEqualTo("No value present");
    }


    @Test
    void 상품_조회() {
       ItemDto album = new ItemDto();
       album.setName("album1");
       album.setPrice(1000);
       album.setStockQuantity(10);
       album.setType("A");
       album.setArtist("artist1");
       album.setEtc("etc1");
       album.setItemStatus("FOR_SALE");

       itemService.saveItem(album);

       ItemDto book = new ItemDto();
       book.setName("book1");
       book.setPrice(1000);
       book.setStockQuantity(10);
       book.setType("B");
       book.setAuthor("author1");
       book.setIsbn("111-111-1111");
       book.setItemStatus("FOR_SALE");

       itemService.saveItem(book);

       assertThat(itemService.findItems().size()).isEqualTo(18+2);
   }

    @Test
    void 상품명으로_조회() {
       // given
       ItemDto book = new ItemDto();
       book.setName("book1");
       book.setPrice(1000);
       book.setStockQuantity(10);
       book.setType("B");
       book.setAuthor("author1");
       book.setIsbn("111-111-1111");
       book.setItemStatus("FOR_SALE");

       long bookId = itemService.saveItem(book);
       // when
       List<Item> items = itemService.findItems(new ItemSearch("book1", null));

       // then
       assertThat(items.contains(itemService.findOne(bookId))).isEqualTo(true);
    }

    /** ------------------ **/


    /** 사용자 기능 **/

    @Test
    void findAllPageable() {
        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("FOR_SALE");

        long bookId = itemService.saveItem(book);

        // when
        Page<Item> items = itemService.findAllPageable("%", PageRequest.of(0, 20));
        Page<Item> findBookByName = itemService.findAllPageable("book1", PageRequest.of(0, 20));
        Page<Item> findAlbumByName = itemService.findAllPageable("album1", PageRequest.of(0, 20));

        // then
        assertThat(items.getTotalElements()).isEqualTo(18+2-1); // HIDE : 1
        assertThat(findBookByName.getContent().contains(itemService.findOne(bookId))).isTrue();
        assertThat(findAlbumByName.getContent().contains(itemService.findOne(albumId))).isTrue();
    }

    @Test
    void findAlbumPageable() {

        // given
        ItemDto album = new ItemDto();
        album.setName("album1");
        album.setPrice(1000);
        album.setStockQuantity(10);
        album.setType("A");
        album.setArtist("artist1");
        album.setEtc("etc1");
        album.setItemStatus("FOR_SALE");

        long albumId = itemService.saveItem(album);

        // when
        Page<Album> findAlbumByName = itemService.findAlbumPageable("album1", PageRequest.of(0, 20));


        // then
        assertThat(findAlbumByName.getContent().contains(itemService.findOne(albumId))).isTrue();

    }

    @Test
    void findBookPageable() {
        // given

        ItemDto book = new ItemDto();
        book.setName("book1");
        book.setPrice(1000);
        book.setStockQuantity(10);
        book.setType("B");
        book.setAuthor("author1");
        book.setIsbn("111-111-1111");
        book.setItemStatus("FOR_SALE");

        long bookId = itemService.saveItem(book);

        // when
        Page<Book> findBookByName = itemService.findBookPageable("book1", PageRequest.of(0, 20));


        // then
        assertThat(findBookByName.getContent().contains(itemService.findOne(bookId))).isTrue();

    }


}