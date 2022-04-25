package dare.daremall.controller.order;

import dare.daremall.controller.member.BaggedItemDto;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderForm {

    private List<BaggedItemDto> list = new ArrayList<>();

}
