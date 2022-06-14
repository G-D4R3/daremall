package dare.daremall.controller;

import dare.daremall.controller.admin.AdForm;
import dare.daremall.domain.ad.Ad;
import dare.daremall.repository.AdRepository;
import dare.daremall.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping(value = "/ad")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @PostMapping(value = "/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newAd(AdForm adForm, MultipartFile imgFile) throws IOException {
        if(!imgFile.isEmpty()) {
            String oriImgName = imgFile.getOriginalFilename();
            String imgName = "";

            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/images/ad/main/";
            String savedFileName = oriImgName;
            imgName = savedFileName;
            File saveFile = new File(projectPath, imgName);
            imgFile.transferTo(saveFile);

            adForm.setImagePath("/images/ad/main/" + imgName);
        }
        else {
            throw new IllegalStateException("광고 이미지를 추가해주세요");
        }
        adService.save(adForm);
        return "redirect:/admin/ad";
    }
}
