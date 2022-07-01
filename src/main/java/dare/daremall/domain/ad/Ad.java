package dare.daremall.domain.ad;

import dare.daremall.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ads")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 부모 클래스에 달아준다.
@DiscriminatorColumn(name = "dtype")
public abstract class Ad {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Long id;

    private String name;
    private String imagePath;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AdStatus status;

    private String href;
}
