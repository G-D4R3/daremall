package dare.daremall.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dare.daremall.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name="member")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;
    private String loginId;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ShoppingBag shoppingBag;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LikeItem> likes = new ArrayList<>();


    public Member(String name, String loginId, String password, Address address) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.address = address;
        this.role = MemberRole.USER;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }

    public void addLikeItem(LikeItem likeItem) {
        for(LikeItem like : likes) {
            if(like.getItem()== likeItem.getItem()) return;
        }
        likes.add(likeItem);
        likeItem.setMember(this);
    }

    public void removeLikeItem(LikeItem likeItem) {
        likes.remove(likeItem);
        likeItem.setMember(null);
    }
}
