package dare.daremall.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dare.daremall.domain.item.Item;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class BaggedItem {

    @Id @GeneratedValue
    @Column(name = "bagged_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_bag_id")
    @JsonIgnore
    private ShoppingBag shoppingBag;

    private int totalPrice;

    private int count;
}
