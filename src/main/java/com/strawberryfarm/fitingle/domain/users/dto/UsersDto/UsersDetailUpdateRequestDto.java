package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDetailUpdateRequestDto implements BaseDto {
	private String profileUrl;
	private String nickname;
	private String aboutMe;

	@Override
	public ResultDto doResultDto(String message, String errorCode) {
		return ResultDto.builder()
			.message(message)
			.data(this)
			.errorCode(errorCode)
			.build();
	}
}
