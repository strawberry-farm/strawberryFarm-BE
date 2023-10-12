package com.strawberryfarm.fitingle.config;

import com.strawberryfarm.fitingle.security.JwtAuthorizationFilter;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.csrf().disable()//csrf 서버에 인증 정보를 저장하지 않겠다.
				.authorizeHttpRequests().antMatchers("/**").permitAll();
//		실제 운영할 때 접근 막
//		http.authorizeHttpRequests()
//			.antMatchers(
//				"/auth/signup",
//				"/auth/email-request",
//				"/auth/email-confirm",
//				"/auth/login",
//				"/auth/password-request",
//				"/auth/password-confirm",
//				"/auth/password-edit",
//				"/auth/logout").permitAll();
		http.formLogin().disable()
			.addFilterBefore(this.jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests().antMatchers("/**").permitAll();

		return http.build();
	}



}
