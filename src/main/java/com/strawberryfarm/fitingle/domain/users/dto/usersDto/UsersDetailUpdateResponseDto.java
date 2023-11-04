package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UsersDetailUpdateResponseDto extends BaseDto {
	private Long userId;
	private String email;
	private String profileUrl;
	private String nickname;
	private String aboutMe;
}
