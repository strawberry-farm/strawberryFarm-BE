package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class UsersAccessTokenRefreshResponseDto extends BaseDto {
	private String email;
	private String accessToken;
}
