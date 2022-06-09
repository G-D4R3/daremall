package dare.daremall.service;

import dare.daremall.domain.item.Album;
import dare.daremall.domain.item.Book;
import dare.daremall.domain.item.Item;
import dare.daremall.domain.item.ItemSearch;
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

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) { // UpdateItemDto로 만들어서 관리해도 됨

        // 영속성 엔티티의 변경감지 사용
        Item findItem = itemRepository.findById(itemId).orElse(null);

        if(findItem!=null) {
            // findItem.change(price, stockQuantity, name); 등 의미있는 메소드로 작성하는 편이 좋다.
            findItem.setName(name);
            findItem.setPrice(price);
            findItem.setStockQuantity(stockQuantity);
        }
        itemRepository.save(findItem);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
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
}
