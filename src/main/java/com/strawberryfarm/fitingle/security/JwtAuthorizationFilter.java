package com.strawberryfarm.fitingle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	private final RedisTemplate redisTemplate;
	private final JwtTokenManager jwtTokenManager;

//	@Override
//	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
//		throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest) servletRequest;
//		HttpServletResponse response = (HttpServletResponse) servletResponse;
//		String jwt = request.getHeader(TOKEN_HEADER);
//		ResultDto errorResultDto = new ResultDto();
//		String refreshToken = "";
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if (cookie.getName().equals("refreshToken")) {
//					refreshToken = cookie.getValue();
//				}
//			}
//		}
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		if (ObjectUtils.isEmpty(jwt)) { //토큰이 들어 있는지 확인
//			// 토큰이 없는 요청이다. 라는 로그 남기기
//			response.setStatus(HttpServletResponse.SC_OK);
//			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//			response.getWriter().write(objectMapper.writeValueAsString(
//				ResultDto.builder()
//					.message("accessToken Error : empty accesstoken")
//					.data(null)
//					.errorCode("0107")
//					.build()
//			));
//			return;
//
//		} else if(!jwt.startsWith(TOKEN_PREFIX)){ //토큰 타입 확인
//			// Bearer 방식이 아니다 Bearer은 토큰 인증 방식을 말한다. 라는 로그 남기기
//			response.setStatus(HttpServletResponse.SC_OK);
//			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//			response.getWriter().write(objectMapper.writeValueAsString(
//				ResultDto.builder()
//					.message("accessToken Error : Token type Invalid")
//					.data(null)
//					.errorCode("0108")
//					.build()
//			));
//			return;
//		} else {
//			if (jwtTokenManager.refreshTokenValidate(refreshToken,errorResultDto)) {
//				if (!redisTemplate.opsForValue().get(jwtTokenManager.getAuthName(refreshToken)).equals(refreshToken)) {
//					response.setStatus(HttpServletResponse.SC_OK);
//					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//					response.getWriter().write(objectMapper.writeValueAsString(ResultDto.builder()
//						.message("authentication error : Already Login Other Device")
//						.data(null)
//						.errorCode("0109")
//						.build()));
//					return;
//				}
//				if (jwtTokenManager.accessTokenValidate(jwt,errorResultDto)) {
//					Authentication authentication = jwtTokenManager.getAuthentication(jwt);
//					SecurityContextHolder.getContext().setAuthentication(authentication);
//				} else {
//					response.setStatus(HttpServletResponse.SC_OK);
//					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//					response.getWriter().write(objectMapper.writeValueAsString(errorResultDto));
//					return;
//				}
//			} else {
//				response.setStatus(HttpServletResponse.SC_OK);
//				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//				response.getWriter().write(objectMapper.writeValueAsString(errorResultDto));
//				return;
//			}
//		}
//
//		chain.doFilter(request,response);
//	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String jwt = request.getHeader(TOKEN_HEADER);
		ResultDto errorResultDto = new ResultDto();
		String refreshToken = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					refreshToken = cookie.getValue();
				}
			}
		}

		ObjectMapper objectMapper = new ObjectMapper();
		if (!ObjectUtils.isEmpty(jwt)) {
			if(!jwt.startsWith(TOKEN_PREFIX)){ //토큰 타입 확인
				// Bearer 방식이 아니다 Bearer은 토큰 인증 방식을 말한다. 라는 로그 남기기
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write(objectMapper.writeValueAsString(
					ResultDto.builder()
						.message("accessToken Error : Token type Invalid")
						.data(null)
						.errorCode("0108")
						.build()
				));
				return;
			} else {
				if (jwtTokenManager.refreshTokenValidate(refreshToken,errorResultDto)) {
					if (!redisTemplate.opsForValue().get(jwtTokenManager.getAuthName(refreshToken)).equals(refreshToken)) {
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType(MediaType.APPLICATION_JSON_VALUE);
						response.getWriter().write(objectMapper.writeValueAsString(ResultDto.builder()
							.message("authentication error : Already Login Other Device")
							.data(null)
							.errorCode("0109")
							.build()));
						return;
					}
					if (jwtTokenManager.accessTokenValidate(jwt,errorResultDto)) {
						Authentication authentication = jwtTokenManager.getAuthentication(jwt);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					} else {
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType(MediaType.APPLICATION_JSON_VALUE);
						response.getWriter().write(objectMapper.writeValueAsString(errorResultDto));
						return;
					}
				} else {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.getWriter().write(objectMapper.writeValueAsString(errorResultDto));
					return;
				}
			}
		}

		filterChain.doFilter(request,response);
	}
}
