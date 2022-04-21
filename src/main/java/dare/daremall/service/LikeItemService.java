package dare.daremall.service;

import dare.daremall.domain.LikeItem;
import dare.daremall.domain.item.Item;
import dare.daremall.repository.LikeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeItemService {

    private final LikeItemRepository likeItemRepository;

    @Transactional
    public void save(LikeItem likeItem) {
        likeItemRepository.save(likeItem);
    }

    public LikeItem findOne(Long memberId, Long itemId) {
        return likeItemRepository.findByIds(memberId, itemId).orElse(null);
    }

    @Transactional
    public void remove(LikeItem likeItem) {
        likeItemRepository.remove(likeItem);
    }
}
