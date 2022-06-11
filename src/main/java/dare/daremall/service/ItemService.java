package dare.daremall.service;

import dare.daremall.controller.item.ItemDto;
import dare.daremall.domain.item.*;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BaggedItemRepository baggedItemRepository;

    @Transactional
    public void saveItem(ItemDto itemDto) {
        if(itemDto.getType().equals("B")) {
            Book book = new Book();
            book.setName(itemDto.getName());
            book.setPrice(itemDto.getPrice());
            book.setStockQuantity(itemDto.getStockQuantity());
            book.setAuthor(itemDto.getAuthor());
            book.setIsbn(itemDto.getIsbn());
            book.setImagePath(itemDto.getImagePath());
            book.setItemStatus(ItemStatus.valueOf(itemDto.getItemStatus()));
            itemRepository.save(book);
        }
        else if(itemDto.getType().equals("A")) {
            Album album = new Album();
            album.setName(itemDto.getName());
            album.setPrice(itemDto.getPrice());
            album.setStockQuantity(itemDto.getStockQuantity());
            album.setArtist(itemDto.getArtist());
            album.setEtc(itemDto.getEtc());
            album.setImagePath(itemDto.getImagePath());
            album.setItemStatus(ItemStatus.valueOf(itemDto.getItemStatus()));
            itemRepository.save(album);
        }
        else {
            throw new IllegalStateException("상품을 추가할 수 없습니다.");
        }
    }

    @Transactional
    public void updateItem(ItemDto itemDto) { // UpdateItemDto로 만들어서 관리해도 됨

        // Long itemId, String name, int price, int stockQuantity
        // 영속성 엔티티의 변경감지 사용
        Item findItem = itemRepository.findById(itemDto.getId()).orElse(null);

        if(findItem!=null) {
            // findItem.change(price, stockQuantity, name); 등 의미있는 메소드로 작성하는 편이 좋다.
            findItem.setName(itemDto.getName());
            findItem.setPrice(itemDto.getPrice());
            findItem.setStockQuantity(itemDto.getStockQuantity());
            if(findItem.getClass().equals(Album.class)) {
                ((Album) findItem).setArtist(itemDto.getArtist());
                ((Album) findItem).setEtc(itemDto.getEtc());
            }
            else if(findItem.getClass().equals(Book.class)) {
                ((Book) findItem).setAuthor(itemDto.getAuthor());
                ((Book) findItem).setIsbn(itemDto.getIsbn());
            }
            if(itemDto.getImagePath()!=null) {
                findItem.setImagePath(itemDto.getImagePath());
            }
            findItem.setItemStatus(ItemStatus.valueOf(itemDto.getItemStatus()));
        }
        itemRepository.save(findItem);
        if(ItemStatus.valueOf(itemDto.getItemStatus())!=ItemStatus.FOR_SALE){
            baggedItemRepository.removeByItemId(findItem.getId());
        }
        baggedItemRepository.updateCount(findItem.getId(), itemDto.getStockQuantity());

    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public List<Item> findItemsExceptHide() {
        return itemRepository.findExceptHide();
    }

    public Item findOne(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> findByName(String name) {
        return itemRepository.findByName(name);
    }

    public List<Album> findAlbums() {
        return itemRepository.findByType("album");
    }

    public List<Book> findBooks() {
        return itemRepository.findByType("book");
    }

    public List<Item> findItems(ItemSearch itemSearch) {
        return itemRepository.findByName(itemSearch.getName());
    }

    public List<Book> findBooks(ItemSearch itemSearch) {
        return itemRepository.findBookByName(itemSearch.getName());
    }

    public List<Album> findAlbums(ItemSearch itemSearch) {
        return itemRepository.findAlbumByName(itemSearch.getName());
    }

    @Transactional
    public void delete(Long itemId) {
        itemRepository.remove(itemId);
    }
}
