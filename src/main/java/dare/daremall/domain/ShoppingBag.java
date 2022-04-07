package dare.daremall.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ShoppingBag {

    @Id @GeneratedValue
    @Column(name = "shopping_bag_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "shoppingBag", cascade = CascadeType.ALL)
    private List<BaggedItem> baggedItems = new ArrayList<>();
}
