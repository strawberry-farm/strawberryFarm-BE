package com.strawberryfarm.fitingle;

import com.strawberryfarm.fitingle.domain.adminarea.service.AdminAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class FitingleApplication implements CommandLineRunner {

	private final AdminAreaService adminAreaService;

	public static void main(String[] args) {
		SpringApplication.run(FitingleApplication.class, args);
	}

	// 처음 어플리케이션 시작시 (행정구역 코드 API -> DB)실행
	@Override
	public void run(String... args) throws Exception {
		adminAreaService.insertRegionCodes();
	}

	// 매주 일요일 자정에 (행정구역 코드 API -> DB)실행
	@Scheduled(cron = "0 0 0 * * SUN")
	public void scheduledRegionDeleteAndInsert() {
		adminAreaService.updateRegionCodes();
	}
}
