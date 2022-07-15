package dare.daremall.service;

import dare.daremall.controller.admin.AdForm;
import dare.daremall.domain.ad.Ad;
import dare.daremall.domain.ad.AdStatus;
import dare.daremall.domain.ad.MainAd;
import dare.daremall.exception.CannotAddNewAdException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class AdServiceTest {

    @Autowired AdService adService;

    /** 관리자 **/

    @Test
    public void 광고_추가() {
        // given
        AdForm ad = new AdForm();
        ad.setName("광고1");
        ad.setType("main");
        ad.setImagePath("/image.png");
        ad.setStartDate("2022-01-01");
        ad.setEndDate("2022-12-31");
        ad.setStatus("NOW");

        // when
        long adId = adService.save(ad);
        Ad findAd = adService.findOne(adId);

        // then
        assertThat(findAd.getName()).isEqualTo("광고1");

    }

    @Test
    public void 광고_추가_실패() {
        // given
        AdForm ad = new AdForm();

        // when
        CannotAddNewAdException e = assertThrows(CannotAddNewAdException.class, () -> adService.save(ad));

        // then
        assertThat(e.getMessage()).isEqualTo("광고를 추가할 수 없습니다.");
    }

    @Test
    public void 광고_삭제() {
        // given
        AdForm ad = new AdForm();
        ad.setName("광고1");
        ad.setType("main");
        ad.setImagePath("/image.png");
        ad.setStartDate("2022-01-01");
        ad.setEndDate("2022-12-31");
        ad.setStatus("NOW");
        long adId = adService.save(ad);

        // when
        adService.remove(adId);

        // then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> adService.findOne(adId));
        assertThat(e.getMessage()).isEqualTo("No value present");

    }

    @Test
    public void 광고_수정() {
        // given
        AdForm ad = new AdForm();
        ad.setName("광고1");
        ad.setType("main");
        ad.setImagePath("/image.png");
        ad.setStartDate("2022-01-01");
        ad.setEndDate("2022-12-31");
        ad.setStatus("NOW");
        long adId = adService.save(ad);

        // when
        Ad findAd = adService.findOne(adId);
        AdForm updateAd = new AdForm();
        updateAd.setId(findAd.getId());
        updateAd.setName("광고2");
        updateAd.setType("main");
        updateAd.setImagePath("/image2.png");
        updateAd.setStartDate("2022-01-03");
        updateAd.setEndDate("2022-12-30");
        updateAd.setStatus("NOT");

        adService.update(updateAd);

        // then
        Ad updated = adService.findOne(findAd.getId());
        assertThat(updated.getName()).isEqualTo(updateAd.getName());
        assertThat(updated.getImagePath()).isEqualTo(updateAd.getImagePath());
        assertThat(updated.getStartDate()).isEqualTo(LocalDate.parse(updateAd.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertThat(updated.getEndDate()).isEqualTo(LocalDate.parse(updateAd.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertThat(updated.getStatus()).isEqualTo(AdStatus.valueOf(updateAd.getStatus()));

    }

    /** ------------------ **/

    @Test
    public void findMainAdNow() {
        // given
        AdForm ad = new AdForm();
        ad.setName("광고1");
        ad.setType("main");
        ad.setImagePath("/image.png");
        ad.setStartDate("2022-01-01");
        ad.setEndDate("2022-12-31");
        ad.setStatus("NOW");
        long adId = adService.save(ad);

        // when
        List<MainAd> mainAdNow = adService.findMainAdNow();
        MainAd findAd = (MainAd) adService.findOne(adId);

        // then
        assertThat(mainAdNow.contains(findAd)).isTrue();

    }

    @Test
    public void findMainAd() {
        // given
        AdForm ad = new AdForm();
        ad.setName("광고1");
        ad.setType("main");
        ad.setImagePath("/image.png");
        ad.setStartDate("2022-01-01");
        ad.setEndDate("2022-12-31");
        ad.setStatus("NOW");
        long adId = adService.save(ad);

        // when
        List<MainAd> mainAds = adService.findMainAd();
        MainAd findAd = (MainAd) adService.findOne(adId);

        // then
        assertThat(mainAds.contains(findAd)).isTrue();

    }

}