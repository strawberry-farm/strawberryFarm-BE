package com.strawberryfarm.fitingle.security;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

		if (ObjectUtils.isEmpty(jwt)) {
			log.info("request에 토큰이 없습니다.");
			jwt = null;
		} else if (!jwt.startsWith(TOKEN_PREFIX)) {
			log.info("토큰 시작이 다릅니다.");
			jwt = null;
		} else {
			jwt = jwt.substring(TOKEN_PREFIX.length());
			// 토큰의 유효성 검사
			if (jwtTokenManager.accessTokenValidate(jwt)) {
				Authentication auth = jwtTokenManager.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				log.warn("Invalid token.");
			}
		}
		filterChain.doFilter(request, response);
	}
}
