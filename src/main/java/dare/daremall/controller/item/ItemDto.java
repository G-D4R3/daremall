package dare.daremall.controller.item;

import dare.daremall.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class ItemDto {

    @NotEmpty
    private Long id;

    // 세 개 다 not empty
    @NotBlank(message = "상품명을 입력해주세요")
    private String name;
    @NotBlank(message = "상품 가격을 입력해주세요")
    private int price;
    @NotBlank(message = "재고 수량을 입력해주세요")
    private int stockQuantity;

    private String author;
    private String isbn;

    private String artist;
    private String etc;

    @NotBlank(message = "상품 이미지를 업로드 해주세요")
    private String imageUrl;

    @NotBlank(message = "상품 분류를 선택해주세요")
    private String type;

}
