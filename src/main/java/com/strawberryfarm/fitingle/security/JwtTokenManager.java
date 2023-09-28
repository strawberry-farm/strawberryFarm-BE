package com.strawberryfarm.fitingle.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
public class JwtTokenManager {
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000*60*60;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000*60*60*24;
	private final Key key;

	public JwtTokenManager(@Value("${jwt.secretKey}")
						   String secretKey) {
		byte[] byteKey = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(byteKey);
	}

	public boolean accessTokenValidate(String jwt) {
		if (!StringUtils.hasText(jwt)) {
			// 토큰이 없다, 로그 남기기
			return false;
		}

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			System.out.println("Invalid JWT Token");
		} catch (ExpiredJwtException e) {
			System.out.println("Expired JWT Token");
		} catch (UnsupportedJwtException e) {
			System.out.println("Unsupported JWT Token");
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty");
		}

		return false;
	}

	public Authentication getAuthentication(String jwt) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

		if (claims.get("auth") == null) {
			return null;
		}

		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get("auth").toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());


		UserDetails userDetails = new User(claims.getSubject(),"",authorities);
		return new UsernamePasswordAuthenticationToken(userDetails,"",authorities);
 	}

	public String genAccessToken(String userName, Authentication authentication) {
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+ACCESS_TOKEN_EXPIRE_TIME);

		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		Map<String,String> authoritiesMap = new HashMap<>();

		authoritiesMap.put("auth",authorities);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
				.setHeaderParam("typ", "jwt")
				.setSubject(userName)
				.setClaims(authoritiesMap)
				.setIssuer("fitingle")
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(expireDate)
				.compact();
	}

	public String genRefreshToken(String userName) {
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+REFRESH_TOKEN_EXPIRE_TIME);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
				.setHeaderParam("typ", "jwt")
				.setSubject(userName)
				.setIssuer("fitingle")
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(expireDate)
				.compact();
	}

	public String genToken(Long second) {
		Long expireTime = 1000*second;
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+expireTime);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
				.setHeaderParam("typ", "jwt")
				.setIssuer("GaJaMy")
				.setSubject("subject")
				.claim("name", "사용자")
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(expireDate)
				.compact();
	}



}
