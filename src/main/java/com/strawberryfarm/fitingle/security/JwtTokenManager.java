package com.strawberryfarm.fitingle.security;

import com.strawberryfarm.fitingle.dto.ResultDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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


	public boolean accessTokenValidate(String accessToken, ResultDto result) {
		if (!StringUtils.hasText(accessToken)) {
			// 토큰이 없다, 로그 남기기
			result.setResultData("accessToken Exception : Invalid JWT Token" ,null, "0100");
			return false;
		}

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			System.out.println("Invalid JWT Token");
			result.setResultData("accessToken Exception : Invalid JWT Token" ,null, "0100");
		} catch (ExpiredJwtException e) {
			System.out.println("Expired JWT Token");
			result.setResultData("accessToken Exception : Expired JWT Token" ,null, "0101");
		} catch (UnsupportedJwtException e) {
			System.out.println("Unsupported JWT Token");
			result.setResultData("accessToken Exception : Unsupported JWT Token" ,null, "0102");
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty");
			result.setResultData("accessToken Exception : JWT claims string is empty" ,null, "0103");
		}

		return false;
	}

	public boolean refreshTokenValidate(String refreshToken,ResultDto result) {
		if (!StringUtils.hasText(refreshToken)) {
			// 토큰이 없다, 로그 남기기
			result.setResultData("refreshToken Exception : Invalid JWT Token" ,null, "0100");
			return false;
		}

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			System.out.println("Invalid JWT Token");
			result.setResultData("refreshToken Exception : Invalid JWT Token" ,null, "0104");
		} catch (ExpiredJwtException e) {
			System.out.println("Expired JWT Token");
			result.setResultData("refreshToken Exception : Expired JWT Token" ,null, "0105");
		} catch (UnsupportedJwtException e) {
			System.out.println("Unsupported JWT Token");
			result.setResultData("refreshToken Exception : Unsupported JWT Token" ,null, "0106");
		}

		return false;
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

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

	 public String getAuthName(String refreshToken) {
		 Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();

		 if (claims.get("auth") == null) {
			 return null;
		 }

		 return claims.getSubject();
	}

	public String genAccessToken(Authentication authentication) {
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+ACCESS_TOKEN_EXPIRE_TIME);

		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		Map<String,String> authoritiesMap = new HashMap<>();

		authoritiesMap.put("auth",authorities);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
				.setHeaderParam("typ", "jwt")
				.setSubject(authentication.getName())
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

	private ResultDto setResultDto(String message, String errorCode) {
		return ResultDto.builder()
			.message(message)
			.data(null)
			.errorCode(errorCode)
			.build();
	}

	public Long getAccessTokenExpiredTime() {
		return ACCESS_TOKEN_EXPIRE_TIME;
	}

	public Long getRefreshTokenExpiredTime() {
		return REFRESH_TOKEN_EXPIRE_TIME;
	}

}
