package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDetailUpdateRequestDto {
	private String profileUrl;
	private String nickname;
	private String aboutMe;
}
