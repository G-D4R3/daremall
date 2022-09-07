package dare.daremall.item.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ItemDto {

    private Long id;

    // 세 개 다 not empty
    @NotBlank(message = "상품명을 입력해주세요")
    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;

    private String artist;
    private String etc;

    // @NotBlank(message = "상품 이미지를 업로드 해주세요")
    private String imagePath;

    private String type;

    private String itemStatus;

}
