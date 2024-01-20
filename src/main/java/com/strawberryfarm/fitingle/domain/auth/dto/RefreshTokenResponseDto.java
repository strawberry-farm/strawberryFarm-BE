package com.strawberryfarm.fitingle.domain.auth.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
public class RefreshTokenResponseDto extends BaseDto {
	private String accessToken;
}
