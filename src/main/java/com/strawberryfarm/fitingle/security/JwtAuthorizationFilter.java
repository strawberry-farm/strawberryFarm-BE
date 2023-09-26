package com.strawberryfarm.fitingle.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	private final JwtTokenManager jwtTokenManager;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String jwt = request.getHeader(TOKEN_HEADER);

		if (ObjectUtils.isEmpty(jwt)) { //토큰이 들어 있는지 확인
			// 토큰이 없는 요청이다. 라는 로그 남기기
			jwt = null;
		} else if(!jwt.startsWith(TOKEN_PREFIX)){ //토큰 타입 확인
			// Bearer 토큰이 아니다 라는 로그 남기기
			jwt = null;
		}

		jwt = jwt.substring(TOKEN_PREFIX.length());


	}
}
