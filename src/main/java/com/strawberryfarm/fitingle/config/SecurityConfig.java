package com.strawberryfarm.fitingle.config;

import com.strawberryfarm.fitingle.security.EntryPoint.JwtAuthenticationEntryPoint;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import com.strawberryfarm.fitingle.security.filters.ExceptionHandleFilter;
import com.strawberryfarm.fitingle.security.filters.JwtAuthorizationFilter;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
	private final JwtTokenManager jwtTokenManager;
	private final RedisTemplate redisTemplate;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


		http.csrf().disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.cors().configurationSource(configurationSource());
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(new JwtAuthorizationFilter(redisTemplate,jwtTokenManager), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandleFilter(), JwtAuthorizationFilter.class)
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


		// 나중에 swagger 접근 허용 추가해야함
		return http.build();
	}


	@Bean
	public CorsConfigurationSource configurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**",configuration);
		return source;
	}

}
