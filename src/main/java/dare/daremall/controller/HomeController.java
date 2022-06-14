package dare.daremall.controller;

import dare.daremall.domain.ad.MainAd;
import dare.daremall.service.AdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired AdService adService;

    @RequestMapping("/")
    public String home(Model model) {
        List<MainAd> ads = adService.findMainAd();
        model.addAttribute("ads", ads);
        return "home";
    }
}

