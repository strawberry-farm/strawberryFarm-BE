package com.strawberryfarm.fitingle.config;

import com.strawberryfarm.fitingle.security.JwtAuthorizationFilter;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private JwtTokenManager jwtTokenManager;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.csrf().disable()//csrf 서버에 인증 정보를 저장하지 않겠다.
				.authorizeHttpRequests().antMatchers("/**").permitAll()
				.and()
				.formLogin().disable()
				.addFilterBefore(new JwtAuthorizationFilter(jwtTokenManager), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
