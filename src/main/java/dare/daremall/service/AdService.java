package dare.daremall.service;

import dare.daremall.controller.admin.AdForm;
import dare.daremall.domain.ad.Ad;
import dare.daremall.domain.ad.AdStatus;
import dare.daremall.domain.ad.MainAd;
import dare.daremall.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;

    @Transactional
    public void save(AdForm adForm) {
        if(adForm.getType().equals("main")) {
            MainAd ad = new MainAd();
            ad.setName(adForm.getName());
            ad.setImagePath(adForm.getImagePath());
            ad.setStart(LocalDate.parse(adForm.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setEnd(LocalDate.parse(adForm.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setStatus(AdStatus.valueOf(adForm.getStatus()));
            ad.setHref(adForm.getHref());
            adRepository.save(ad);
        }
    }

    public List<String> getMainAdSrc() {
        return adRepository.findMainAdNow().stream().map(ma->ma.getImagePath()).collect(Collectors.toList());
    }

    public List<MainAd> findMainAd() {
        return adRepository.findMainAd();
    }

    @Transactional
    public void remove(Long adId) {
        adRepository.remove(adId);
    }

    public Ad find(Long adId) {
        return adRepository.findOne(adId);
    }

    @Transactional
    public void update(AdForm adForm) {
        if(adForm.getType().equals("main")) {
            MainAd ad = (MainAd) adRepository.findOne(adForm.getId());
            ad.setName(adForm.getName());
            if(adForm.getImagePath() != null) ad.setImagePath(adForm.getImagePath());
            ad.setStart(LocalDate.parse(adForm.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setEnd(LocalDate.parse(adForm.getEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setStatus(AdStatus.valueOf(adForm.getStatus()));
            ad.setHref(adForm.getHref());
            adRepository.save(ad);
        }
    }

    public List<MainAd> findMainAdNow() {
        return adRepository.findMainAdNow();
    }
}
