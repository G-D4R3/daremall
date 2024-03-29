package dare.daremall.member.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dare.daremall.order.domains.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name="member")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String name;
    private String loginId;
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeItem> likes = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaggedItem> shoppingBag = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryInfo> deliveryInfos = new ArrayList<>();


    public Member(String name, String loginId, String password, String phone, Address address) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.phone = phone;
        this.role = MemberRole.ROLE_USER;
        this.address = address;

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setNickname("기본주소");
        deliveryInfo.setName(this.name);
        deliveryInfo.setPhone(this.phone);
        deliveryInfo.setAddress(address);
        deliveryInfo.setIsDefault(true);

        this.deliveryInfos.add(deliveryInfo);
        deliveryInfo.setMember(this);
    }

    public void encryptPassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(this.password);
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
        //likeItem.setMember(null);
    }

    public void addBaggedItem(BaggedItem baggedItem) {
        for(BaggedItem item:shoppingBag) {
            if(item.getItem() == baggedItem.getItem()) {
                item.setCount(item.getCount()+ baggedItem.getCount());
                return;
            }
        }
        shoppingBag.add(baggedItem);
        baggedItem.setMember(this);
    }

    public void removeBaggedItem(BaggedItem baggedItem) {
        shoppingBag.remove(baggedItem);
        //baggedItem.setMember(null);
    }

    public void addDelivery(DeliveryInfo deliveryInfo) {
        deliveryInfos.add(deliveryInfo);
        deliveryInfo.setMember(this);
    }

    public void removeDelivery(DeliveryInfo deliveryInfo) {
        deliveryInfos.remove(deliveryInfo);
        //deliveryInfo.setMember(null);
    }

}
