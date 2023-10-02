package com.strawberryfarm.fitingle.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

	private final RedisTemplate<String, String> redisTemplate;
	private Key key;
	@Value("${jwt.secret}")
	private String secretKey;
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000*60*60;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000*60*60*24;

	@PostConstruct
	public void init() {
		/*
		   제공된 Base64로 인코딩된 비밀 키를 디코드한다.
		   이렇게 하면 JWT 토큰을 안전하게 서명할 수 있게 되는데,
		   이는 Base64 인코딩 방식이 원본 키 값을 안전하게 전송하거나 저장하는 데 도움을 주기 때문이다.
		   디코딩된 키를 사용해 HMAC 키를 초기화하면, JWT 생성 및 검증 과정에서 해당 키를 사용한다.
		   (이렇게 안하면 밑줄생김)
		 */
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	//유효한 토근확인
	public boolean accessTokenValidate(String jwt) {
		if (!StringUtils.hasText(jwt)) {
			return false;
		}
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
			return true;
		} catch (JwtException ex) {
			// JWT 유효성 검사 실패, 로그를 남길 수 있습니다.
			return false;
		}
	}

	//access 토큰생성
	public String createAccessToken(Authentication authentication) {
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	//refresh 토큰생성
	public String createRefreshToken(Authentication authentication) {
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

		String refreshToken =  Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		// redis에 저장
		redisTemplate.opsForValue().set(
				authentication.getName(),
				refreshToken,
				REFRESH_TOKEN_EXPIRE_TIME,
				TimeUnit.MILLISECONDS
		);
		return refreshToken;
	}
	
	//클레임 정보를 추출하는 역할
	public Claims getClaimsFromToken(String token) {
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
		return jwtParser.parseClaimsJws(token).getBody();
	}

	//권한 부여
	public Authentication getAuthentication(String token) {
		Claims claims = getClaimsFromToken(token);
		String username = claims.getSubject();

		// 모든 사용자에게 동일한 권한 부여
		List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

		return new UsernamePasswordAuthenticationToken(username, null, authorities);
	}
}
