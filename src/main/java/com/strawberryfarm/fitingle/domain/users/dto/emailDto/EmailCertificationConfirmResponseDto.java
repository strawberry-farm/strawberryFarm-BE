package com.strawberryfarm.fitingle.domain.users.dto.emailDto;

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
public class EmailCertificationConfirmResponseDto implements BaseDto {
	private String email;


	@Override
	public ResultDto doResultDto(String message, String errorCode) {
		return ResultDto.builder()
			.message(message)
			.data(this)
			.errorCode(errorCode)
			.build();
	}
}
