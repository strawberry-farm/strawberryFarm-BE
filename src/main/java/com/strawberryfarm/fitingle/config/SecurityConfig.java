package com.strawberryfarm.fitingle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.csrf().disable()//csrf 서버에 인증 정보를 저장하지 않겠다.
			.authorizeHttpRequests().antMatchers("/**").permitAll()
			.and()
			.formLogin().disable()
			.build();
	}

}
