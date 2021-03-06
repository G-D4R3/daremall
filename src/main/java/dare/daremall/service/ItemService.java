package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.domain.item.*;
import dare.daremall.exception.CannotAddNewItemException;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemJpaRepository;
import dare.daremall.repository.ItemRepository;
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
            throw new CannotAddNewItemException("????????? ????????? ??? ????????????.");
        }
    }

    public Item findOne(Long id) {
        return itemRepository.findById(id).get();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * ????????? ?????? ??????
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


    /** ????????? ????????? **/

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
    public void updateItem(ItemDto itemDto) { // UpdateItemDto??? ???????????? ???????????? ???

        // Long itemId, String name, int price, int stockQuantity
        // ????????? ???????????? ???????????? ??????
        Item findItem = itemRepository.findById(itemDto.getId()).get();

        if(findItem!=null) {
            // findItem.change(price, stockQuantity, name); ??? ???????????? ???????????? ???????????? ?????? ??????.
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
            baggedItemRepository.removeByItemId(findItem.getId());
        }
        baggedItemRepository.updateCount(findItem.getId(), itemDto.getStockQuantity());

    }

    /** **/

}
