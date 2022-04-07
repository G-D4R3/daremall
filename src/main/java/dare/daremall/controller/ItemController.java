package dare.daremall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/items")
public class ItemController {

    @GetMapping(value = "/")
    public @ResponseBody String items() {
        return "items";
    }
}
