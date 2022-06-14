package dare.daremall.controller.admin;

import lombok.Data;

@Data
public class AdForm {

    private Long id;
    private String name;
    private String start;
    private String end;
    private String imagePath;
    private String type;
    private String status;
    private String href;

}
