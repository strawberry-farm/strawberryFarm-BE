package com.strawberryfarm.fitingle.domain.users.dto.interestArea;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InterestAreaDeleteResponseDto extends BaseDto {
	private String email;
}
