package dare.daremall.controller.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdForm {

    private Long id;

    @NotBlank(message = "광고 이름을 입력해주세요")
    private String name;

    @NotBlank(message = "광고 시작 날짜를 선택해주세요")
    private String startDate;
    @NotBlank(message = "광고 종료 날짜를 선택해주세요")
    private String endDate;
    @NotBlank(message = "광고 이미지를 선택해주세요")
    private String imagePath;
    @NotBlank(message = "광고 유형을 선택해주세요")
    private String type;
    @NotBlank(message = "광고 상태를 선택해주세요")
    private String status;
    private String href;

}
