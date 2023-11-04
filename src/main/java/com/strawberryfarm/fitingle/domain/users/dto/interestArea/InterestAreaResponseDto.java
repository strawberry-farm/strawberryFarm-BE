package com.strawberryfarm.fitingle.domain.users.dto.interestArea;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InterestAreaResponseDto extends BaseDto {
	private String sido;
	private String gungu;
	private String b_code;
}
