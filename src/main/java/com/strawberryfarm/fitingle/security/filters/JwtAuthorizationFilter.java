package com.strawberryfarm.fitingle.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.annotation.Trace;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import com.strawberryfarm.fitingle.security.exception.CustomException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer :";
	private final RedisTemplate redisTemplate;
	private final JwtTokenManager jwtTokenManager;

	@Override
	@Trace
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeaderValue = request.getHeader(TOKEN_HEADER);
		String refreshToken = "";

		//쿠키에서 refresh 토큰을 가져옴
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					refreshToken = cookie.getValue();
				}
			}
		}

		if (!ObjectUtils.isEmpty(authorizationHeaderValue)) {
			//토큰 타입 확인
			//Bearer 방식이 아닐 때
			if (!authorizationHeaderValue.startsWith(TOKEN_PREFIX)) {
				//log.info("INVALID_TOKEN_TYPE : Not Bearer");
				throw new CustomException(ErrorCode.INVALID_TOKEN_TYPE);
			}

			ErrorCode errorCode = jwtTokenManager.refreshTokenValidate(refreshToken);

			if (errorCode != ErrorCode.SUCCESS) {
				log.info("Authorization Error : ",errorCode.getMessage(),errorCode.getCode());
				throw new CustomException(errorCode);
			}

			if (redisTemplate.opsForValue().get(jwtTokenManager.getSubject(refreshToken)).equals("logout")) {
				log.info("Logout Users : Please login");
				throw new CustomException(ErrorCode.LOGOUT_USERS);
			}

			//Bearer 를 땐 실제 Access 토큰
			String subAccessToken = authorizationHeaderValue.substring(TOKEN_PREFIX.length());
			errorCode = jwtTokenManager.accessTokenValidate(subAccessToken);

			if (errorCode != ErrorCode.SUCCESS) {
				log.info("Authorization Error : ",errorCode.getMessage(),errorCode.getCode());
				throw new CustomException(errorCode);
			}

			log.info("Success Authorization");
			Authentication authentication = jwtTokenManager.getAuthentication(subAccessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request,response);
	}
}