package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UsersAccessTokenRefreshRequestDto {
	private String email;
	private int expiredTime;
}
