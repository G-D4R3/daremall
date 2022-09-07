package dare.daremall.ad;

import dare.daremall.admin.dtos.AdForm;
import dare.daremall.ad.domains.Ad;
import dare.daremall.ad.domains.AdStatus;
import dare.daremall.ad.domains.MainAd;
import dare.daremall.core.exception.CannotAddNewAdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;

    public Ad findOne(Long adId) {
        return adRepository.findOne(adId).get();
    }

    @Transactional
    public long save(AdForm adForm) {
        if(adForm.getName()==null || adForm.getImagePath()==null || adForm.getType()==null
                || adForm.getStartDate() == null || adForm.getEndDate() == null || adForm.getType() ==null ) {
            throw new CannotAddNewAdException("광고를 추가할 수 없습니다.");
        }
        if(adForm.getType().equals("main")) {
            MainAd ad = new MainAd();
            ad.setName(adForm.getName());
            ad.setImagePath(adForm.getImagePath());
            ad.setStartDate(LocalDate.parse(adForm.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setEndDate(LocalDate.parse(adForm.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if(ad.getEndDate().isBefore(LocalDate.now())) {
                ad.setStatus(AdStatus.END);
            }
            else ad.setStatus(AdStatus.valueOf(adForm.getStatus()));
            ad.setHref(adForm.getHref());
            adRepository.save(ad);
            return ad.getId();
        }
        return -1; // -1로 리턴했을 때 throw exception
    }

    @Transactional
    public void remove(Long adId) {
        adRepository.remove(adId);
    }

    @Transactional
    public void update(AdForm adForm) {
        if(adForm.getType().equals("main")) {
            MainAd ad = (MainAd) adRepository.findOne(adForm.getId()).get();
            ad.setName(adForm.getName()==null? ad.getName():adForm.getName());
            ad.setImagePath(adForm.getImagePath()==null? ad.getImagePath():adForm.getImagePath());
            ad.setStartDate(adForm.getStartDate()==null? ad.getStartDate():LocalDate.parse(adForm.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ad.setEndDate(adForm.getEndDate()==null? ad.getEndDate():LocalDate.parse(adForm.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if(ad.getEndDate().isBefore(LocalDate.now())) {
                ad.setStatus(AdStatus.END);
            }
            else ad.setStatus(adForm.getStatus()==null? ad.getStatus():AdStatus.valueOf(adForm.getStatus()));
            ad.setHref(adForm.getHref());
            adRepository.save(ad);
        }
    }

    // 메인 화면 표시 광고
    @Transactional
    public List<MainAd> findMainAdNow() {
        return adRepository.findMainAdNow();
    }

    public List<MainAd> findMainAd() {
        return adRepository.findMainAd();
    }
}
