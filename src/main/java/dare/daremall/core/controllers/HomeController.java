package dare.daremall.core.controllers;

import dare.daremall.ad.domains.MainAd;
import dare.daremall.ad.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired AdService adService;
    @GetMapping("/")
    public String home(Model model) {

        // 메인 화면 광고 배너
        List<MainAd> ads = adService.findMainAdNow();
        if(ads.size()>0) model.addAttribute("ads", ads);
        else model.addAttribute("ads", new ArrayList<>());

        return "home";
    }
}

