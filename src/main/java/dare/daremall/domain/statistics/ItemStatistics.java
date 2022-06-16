package dare.daremall.domain.statistics;

import dare.daremall.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ItemStatistics {

    @Id
    @GeneratedValue
    @Column(name="item_statistics_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private LocalDate date;

    private Long salesCount;
    private Long revenue;
}
