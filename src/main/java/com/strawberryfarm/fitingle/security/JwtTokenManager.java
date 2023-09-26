package com.strawberryfarm.fitingle.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000*60*60;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000*60*60*24;

	public boolean accessTokenValidate(String jwt) {
		if (StringUtils.hasText(jwt)) {
			// 토큰이 없다, 로그 남기기
			return false;
		}


		return true;
	}

}
