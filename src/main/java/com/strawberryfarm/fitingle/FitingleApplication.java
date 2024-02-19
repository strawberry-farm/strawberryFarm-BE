package com.strawberryfarm.fitingle;

import com.strawberryfarm.fitingle.domain.adminarea.service.AdminAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class FitingleApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitingleApplication.class, args);
	}
}
