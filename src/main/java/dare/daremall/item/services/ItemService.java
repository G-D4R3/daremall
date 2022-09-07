package dare.daremall.item.services;

import dare.daremall.item.domains.*;
import dare.daremall.item.dtos.ItemDto;
import dare.daremall.core.exception.CannotAddNewItemException;
import dare.daremall.member.repositories.BaggedItemRepository;
import dare.daremall.item.repositories.ItemJpaRepository;
import dare.daremall.item.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final BaggedItemRepository baggedItemRepository;

    @Transactional
    public long
    saveItem(ItemDto itemDto) {
        if(itemDto.getType().equals("B")) {
            Book book = new Book();
            book.setName(itemDto.getName());
            book.setPrice(Integer.valueOf(itemDto.getPrice())==null? 0:itemDto.getPrice());
            book.setStockQuantity(Integer.valueOf(itemDto.getStockQuantity())==null? 0:itemDto.getStockQuantity());
            book.setAuthor(itemDto.getAuthor());
            book.setIsbn(itemDto.getIsbn());
            book.setImagePath(itemDto.getImagePath());
            book.setItemStatus(itemDto.getItemStatus()==null? ItemStatus.HIDE:ItemStatus.valueOf(itemDto.getItemStatus()));
            itemRepository.save(book);
            return book.getId();
        }
        else if(itemDto.getType().equals("A")) {
            Album album = new Album();
            album.setName(itemDto.getName());
            album.setPrice(Integer.valueOf(itemDto.getPrice())==null? 0:itemDto.getPrice());
            album.setStockQuantity(Integer.valueOf(itemDto.getStockQuantity())==null? 0:itemDto.getStockQuantity());
            album.setArtist(itemDto.getArtist());
            album.setEtc(itemDto.getEtc());
            album.setImagePath(itemDto.getImagePath());
            album.setItemStatus(itemDto.getItemStatus()==null? ItemStatus.HIDE:ItemStatus.valueOf(itemDto.getItemStatus()));
            itemRepository.save(album);
            return album.getId();
        }
        else {
            throw new CannotAddNewItemException("상품을 추가할 수 없습니다.");
        }
    }

    public Item findOne(Long id) {
        return itemRepository.findById(id).get();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 사용자 상품 조회
     */

    public Page<Item> findAllPageable(String name, Pageable pageable) {
        return itemJpaRepository.findItemsByName(name, pageable);
    }

    public Page<Album> findAlbumPageable(String name, Pageable pageable) {
        return itemJpaRepository.findAlbumsByName(name, pageable);
    }

    public Page<Book> findBookPageable(String name, Pageable pageable) {
        return itemJpaRepository.findBooksByName(name, pageable);
    }


    /** **/


    /** 관리자 페이지 **/

    public List<Item> findItems(ItemSearch itemSearch) {
        return itemRepository.findByName(itemSearch.getName());
    }

/*
    public List<Book> findBooks(ItemSearch itemSearch) {
        return itemRepository.findBookByName(itemSearch.getName());
    }

    public List<Album> findAlbums(ItemSearch itemSearch) {
        return itemRepository.findAlbumByName(itemSearch.getName());
    }*/

    @Transactional
    public void delete(Long itemId) {
        itemRepository.remove(itemId);
    }

    @Transactional
    public void updateItem(ItemDto itemDto) { // UpdateItemDto로 만들어서 관리해도 됨

        // Long itemId, String name, int price, int stockQuantity
        // 영속성 엔티티의 변경감지 사용
        Item findItem = itemRepository.findById(itemDto.getId()).get();

        if(findItem!=null) {
            // findItem.change(price, stockQuantity, name); 등 의미있는 메소드로 작성하는 편이 좋다.
            findItem.setName(itemDto.getName()==null? findItem.getName():itemDto.getName());
            findItem.setPrice(Integer.valueOf(itemDto.getPrice())==null? findItem.getPrice():itemDto.getPrice());
            findItem.setStockQuantity(Integer.valueOf(itemDto.getStockQuantity())==null? findItem.getStockQuantity() : itemDto.getStockQuantity());
            if(findItem.getClass().equals(Album.class)) {
                ((Album) findItem).setArtist(itemDto.getArtist()==null? ((Album) findItem).getArtist() : itemDto.getArtist());
                ((Album) findItem).setEtc(itemDto.getEtc()==null? ((Album) findItem).getEtc() : itemDto.getEtc());
            }
            else if(findItem.getClass().equals(Book.class)) {
                ((Book) findItem).setAuthor(itemDto.getAuthor()==null? ((Book) findItem).getAuthor() : itemDto.getAuthor());
                ((Book) findItem).setIsbn(itemDto.getIsbn()==null? ((Book) findItem).getIsbn() : itemDto.getIsbn());
            }
            if(itemDto.getImagePath()!=null) {
                findItem.setImagePath(itemDto.getImagePath());
            }
            findItem.setItemStatus(itemDto.getItemStatus()==null? findItem.getItemStatus():ItemStatus.valueOf(itemDto.getItemStatus()));
        }
        itemRepository.save(findItem);
        if(itemDto.getItemStatus()==null || !itemDto.getItemStatus().equals("FOR_SALE")){
            baggedItemRepository.removeBaggedItemByItemId(findItem.getId());
        }
        baggedItemRepository.updateBaggedItemCount(findItem.getId(), itemDto.getStockQuantity());

    }

    /** **/

}
