package com.strawberryfarm.fitingle.domain.auth.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthAccessTokenRefreshResponseDto extends BaseDto {
	private String email;
	private String accessToken;
}
