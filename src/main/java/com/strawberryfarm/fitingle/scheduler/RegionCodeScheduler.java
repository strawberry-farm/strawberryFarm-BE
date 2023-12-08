package com.strawberryfarm.fitingle.scheduler;

import com.strawberryfarm.fitingle.domain.adminarea.service.AdminAreaService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionCodeScheduler {

    private final AdminAreaService adminAreaService;

    // 처음 어플리케이션 시작시 (행정구역 코드 API -> DB)실행
    @PostConstruct
    public void initRegionCodes() {
        adminAreaService.insertRegionCodes();
    }

    // 매주 일요일 자정에 (행정구역 코드 API -> DB)실행
    @Scheduled(cron = "0 0 0 * * SUN")
    public void scheduledRegionDeleteAndInsert() {
        adminAreaService.updateRegionCodes();
    }
}