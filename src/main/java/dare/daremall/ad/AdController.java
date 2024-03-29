package dare.daremall.ad;

import dare.daremall.admin.dtos.AdForm;
import dare.daremall.ad.domains.Ad;
import dare.daremall.ad.domains.MainAd;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping(value = "/ad")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @PostMapping(value = "/new")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<String> newAd(@Valid AdForm adForm, MultipartFile imgFile, BindingResult result) throws IOException {

        if(result.hasErrors()) {
            return new ResponseEntity<>("광고 정보를 입력해주세요", HttpStatus.BAD_REQUEST);
        }

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
            return new ResponseEntity<>("광고 이미지를 추가해주세요", HttpStatus.BAD_REQUEST);
        }
        adService.save(adForm);
        return new ResponseEntity<>("광고를 성공적으로 등록했습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity deleteAd(@RequestParam("adId") Long adId) {
        adService.remove(adId);
        return new ResponseEntity("성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

    @PostMapping(value = "/findAd")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody AdForm findAd(@RequestParam("adId") Long adId) {
        Ad ad =  adService.findOne(adId);
        AdForm adForm = new AdForm();
        adForm.setId(ad.getId());
        adForm.setName(ad.getName());
        adForm.setStartDate(ad.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        adForm.setEndDate(ad.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        adForm.setImagePath(ad.getImagePath());
        adForm.setHref(ad.getHref());
        if(ad.getClass().equals(MainAd.class)) {
            adForm.setType("main");
        }
        adForm.setImagePath(ad.getImagePath());
        adForm.setStatus(ad.getStatus().toString());

        return adForm;
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<String> updateAd(@Valid AdForm adForm, MultipartFile imgFile, BindingResult result) throws IOException {

        if(result.hasErrors()) {
            return new ResponseEntity<>("광고 정보를 입력해주세요", HttpStatus.BAD_REQUEST);
        }

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
            adForm.setImagePath(null);
        }
        adService.update(adForm);
        return new ResponseEntity<>("광고를 성공적으로 수정했습니다.", HttpStatus.OK);
    }
}
