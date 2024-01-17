package com.strawberryfarm.fitingle.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthAccessTokenRefreshRequestDto {
	private String email;
	private int expiredTime;
}
