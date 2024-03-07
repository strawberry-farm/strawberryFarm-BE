package com.strawberryfarm.fitingle.security;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.dto.ResultDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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

@Slf4j
@Component
@Getter
public class JwtTokenManager {
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000*60*60;
	private static final long USER_DEFINED_EXPIRED_TIME = 1000;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000*60*60*24;
	private final Key key;

	public JwtTokenManager(@Value("${jwt.secretKey}")
	String secretKey) {
		byte[] byteKey = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(byteKey);
	}


	public boolean accessTokenValidate(String accessToken, ResultDto result) {
		if (!StringUtils.hasText(accessToken)) {
			result.setResultData(ErrorCode.EMPTY_ACCESS_TOKEN.getMessage(), null,ErrorCode.EMPTY_ACCESS_TOKEN.getCode());
			return false;
		}


		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			result.setResultData(ErrorCode.INVALID_ACCESS_TOKEN.getMessage() ,null,ErrorCode.INVALID_ACCESS_TOKEN.getCode());
		} catch (ExpiredJwtException e) {
			result.setResultData(ErrorCode.EXPIRED_ACCESS_TOKEN.getMessage() ,null, ErrorCode.EXPIRED_ACCESS_TOKEN.getCode());
		} catch (UnsupportedJwtException e) {
			result.setResultData(ErrorCode.UNSUPPORTED_ACCESS_TOKEN.getMessage(),null, ErrorCode.UNSUPPORTED_ACCESS_TOKEN.getCode());
		} catch (IllegalArgumentException e) {
			result.setResultData(ErrorCode.EMPTY_CLAIM_ACCESS_TOKEN.getMessage(), null, ErrorCode.EMPTY_CLAIM_ACCESS_TOKEN.getCode());
		}

		return false;
	}

	public ErrorCode accessTokenValidate(String accessToken) {
		if (!StringUtils.hasText(accessToken)) {
			return ErrorCode.EMPTY_ACCESS_TOKEN;
		}


		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
			return ErrorCode.SUCCESS;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			return ErrorCode.INVALID_ACCESS_TOKEN;
		} catch (ExpiredJwtException e) {
			return ErrorCode.EXPIRED_ACCESS_TOKEN;
		} catch (UnsupportedJwtException e) {
			return ErrorCode.UNSUPPORTED_ACCESS_TOKEN;
		} catch (IllegalArgumentException e) {
			return ErrorCode.EMPTY_CLAIM_ACCESS_TOKEN;
		}
	}

	public boolean refreshTokenValidate(String refreshToken,ResultDto result) {
		if (!StringUtils.hasText(refreshToken)) {
			result.setResultData(ErrorCode.EMPTY_REFRESH_TOKEN.getMessage(),null, ErrorCode.EMPTY_REFRESH_TOKEN.getCode());
			return false;
		}

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			result.setResultData(ErrorCode.INVALID_REFRESH_TOKEN.getMessage() ,null, ErrorCode.INVALID_REFRESH_TOKEN.getCode());
		} catch (ExpiredJwtException e) {
			result.setResultData(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage() ,null, ErrorCode.EXPIRED_REFRESH_TOKEN.getCode());
		} catch (UnsupportedJwtException e) {
			result.setResultData(ErrorCode.UNSUPPORTED_REFRESH_TOKEN.getMessage() ,null, ErrorCode.UNSUPPORTED_REFRESH_TOKEN.getCode());
		}

		return false;
	}

	public ErrorCode refreshTokenValidate(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			return ErrorCode.EMPTY_REFRESH_TOKEN;
		}
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
			return ErrorCode.SUCCESS;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			return ErrorCode.INVALID_REFRESH_TOKEN;
		} catch (ExpiredJwtException e) {
			return ErrorCode.EXPIRED_REFRESH_TOKEN;
		} catch (UnsupportedJwtException e) {
			return ErrorCode.UNSUPPORTED_REFRESH_TOKEN;
		}
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

		String test = claims.getSubject();
		UserDetails userDetails = new User(claims.getSubject(),"",authorities);
		return new UsernamePasswordAuthenticationToken(userDetails,"",authorities);
	}

	public String getSubject(String refreshToken) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();

		if (claims.getSubject() == null) {
			return null;
		}

		return claims.getSubject();
	}

	public String genAccessToken(Authentication authentication,Long userId) {
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+ACCESS_TOKEN_EXPIRE_TIME);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Map<String,String> authoritiesMap = new HashMap<>();

		authoritiesMap.put("auth",authorities);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
			.setHeaderParam("typ", "jwt")
			.setClaims(authoritiesMap)
			.setSubject(Long.toString(userId))
			.setIssuer("fitingle")
			.setIssuedAt(Date.from(Instant.now()))
			.setExpiration(expireDate)
			.compact();
	}

	public String genAccessTokenWithExpiredTime(Authentication authentication,Long userId,int expiredTime) {
		long now = (new Date()).getTime();
		Date expireDate = new Date(now+USER_DEFINED_EXPIRED_TIME  * expiredTime);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Map<String,String> authoritiesMap = new HashMap<>();

		authoritiesMap.put("auth",authorities);

		return Jwts.builder().signWith(this.key, SignatureAlgorithm.HS256)
			.setHeaderParam("typ", "jwt")
			.setClaims(authoritiesMap)
			.setSubject(Long.toString(userId))
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