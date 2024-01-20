package com.strawberryfarm.fitingle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer :";
	private final RedisTemplate redisTemplate;
	private final JwtTokenManager jwtTokenManager;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String jwt = request.getHeader(TOKEN_HEADER);

		//결과를 담아줄 겍체
		ResultDto errorResultDto = new ResultDto();

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

		ObjectMapper objectMapper = new ObjectMapper();
		if (!ObjectUtils.isEmpty(jwt)) {
			if(!jwt.startsWith(TOKEN_PREFIX)){ //토큰 타입 확인
				// Bearer 방식이 아니다 Bearer은 토큰 인증 방식을 말한다. 라는 로그 남기기
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write(objectMapper.writeValueAsString(
					ResultDto.builder()
						.message(ErrorCode.INVALID_TOKEN_TYPE.getMessage())
						.data(null)
						.errorCode(ErrorCode.INVALID_TOKEN_TYPE.getCode())
						.build()
				));
				return;
			} else {
				if (jwtTokenManager.refreshTokenValidate(refreshToken,errorResultDto)) {
					if (redisTemplate.opsForValue().get(jwtTokenManager.getSubject(refreshToken)).equals("logout")) {
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType(MediaType.APPLICATION_JSON_VALUE);
						response.getWriter().write(objectMapper.writeValueAsString(ResultDto.builder()
							.message(ErrorCode.LOGOUT_USERS.getMessage())
							.data(null)
							.errorCode(ErrorCode.LOGOUT_USERS.getCode())
							.build()));
						return;
					} /*else if (!redisTemplate.opsForValue().get(jwtTokenManager.getSubject(refreshToken)).equals(refreshToken)) {
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType(MediaType.APPLICATION_JSON_VALUE);
						response.getWriter().write(objectMapper.writeValueAsString(ResultDto.builder()
							.message(ErrorCode.DUPLICATE_LOGIN.getMessage())
							.data(null)
							.errorCode(ErrorCode.DUPLICATE_LOGIN.getCode())
							.build()));
						return;
					}*/

					//실제 access 토큰 인증 부분(subAccessToken 은 "Bearer :"을 떼어낸 것)
					String subAccessToken = jwt.substring(TOKEN_PREFIX.length());

					if (jwtTokenManager.accessTokenValidate(subAccessToken,errorResultDto)) {
						Authentication authentication = jwtTokenManager.getAuthentication(subAccessToken);
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