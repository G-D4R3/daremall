package dare.daremall.controller;

import dare.daremall.domain.ad.MainAd;
import dare.daremall.service.AdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired AdService adService;

    @RequestMapping("/")
    public String home(Model model) {

        // 메인 화면 광고 배너
        List<MainAd> ads = adService.findMainAdNow();
        if(ads.size()>0) model.addAttribute("ads", ads);
        else model.addAttribute("ads", new ArrayList<>());

        return "home";
    }
}

