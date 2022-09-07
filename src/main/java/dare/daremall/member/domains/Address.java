package dare.daremall.member.domains;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String zipcode;
    private String street;
    private String detail;

    protected Address() {
        // JPA 기본 스펙이 reflection이나 proxy등을 써야되는 경우가 많은데 이 때, 기본생성자가 필요하기 때문
        // 따라서 protected로 정의해서 다른데서 막 사용하지 못하도록 표시
    }

    public Address(String zipcode, String street, String detail) {
        this.zipcode = zipcode;
        this.street = street;
        this.detail = detail;
    }
}
