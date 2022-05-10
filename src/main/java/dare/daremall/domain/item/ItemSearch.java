package dare.daremall.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearch {

    private String name;
    private String option;

    public ItemSearch(String name, String option) {
        this.name = name;
        this.option = option;
    }
}
