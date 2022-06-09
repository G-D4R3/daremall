package dare.daremall.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dare.daremall.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaggedItem {

    @Id @GeneratedValue
    @Column(name = "bagged_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    //@JsonIgnore
    private Member member;

    private int price;

    private int count;

    private Boolean checked;

    public static BaggedItem createBaggedItem(Member member, Item item, int price, int count) {
        BaggedItem baggedItem = new BaggedItem();
        baggedItem.setItem(item);
        baggedItem.setMember(member);
        baggedItem.setPrice(price);
        baggedItem.setCount(count);
        baggedItem.setChecked(true);
        return baggedItem;
    }

    public int getTotalPrice() {
        return this.price * this.count;
    }
}
