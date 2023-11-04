package com.strawberryfarm.fitingle.config;

import com.strawberryfarm.fitingle.security.EntryPoint.JwtAuthenticationEntryPoint;
import com.strawberryfarm.fitingle.security.JwtAuthorizationFilter;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenManager jwtTokenManager;
	private final RedisTemplate redisTemplate;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(new JwtAuthorizationFilter(redisTemplate,jwtTokenManager), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers("/**").permitAll()
			.anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());


//
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
//		http.formLogin().disable()
//			.addFilterBefore(this.jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
//			.authorizeHttpRequests().antMatchers("/**","/contents/**").permitAll();
//			.authorizeHttpRequests().anyRequest().permitAll();


		return http.build();
	}



}
