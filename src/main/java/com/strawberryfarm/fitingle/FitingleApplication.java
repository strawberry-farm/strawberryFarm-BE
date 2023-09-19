package com.strawberryfarm.fitingle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FitingleApplication {

	public static void main(String[] args) {
		System.out.println("Start fitingle");
		SpringApplication.run(FitingleApplication.class, args);
	}

}
