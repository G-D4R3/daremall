package dare.daremall.statistics.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class OrderStatistics {

    @Id
    @GeneratedValue
    @Column(name="order_statistics_id")
    private Long id;

    @Column(unique = true)
    private LocalDate date;

    private Long orderQuantity;
    private Long revenue;
}
