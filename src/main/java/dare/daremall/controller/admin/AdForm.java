package dare.daremall.controller.admin;

import lombok.Data;

@Data
public class AdForm {

    private Long id;
    private String name;
    private String startDate;
    private String endDate;
    private String imagePath;
    private String type;
    private String status;
    private String href;

}
