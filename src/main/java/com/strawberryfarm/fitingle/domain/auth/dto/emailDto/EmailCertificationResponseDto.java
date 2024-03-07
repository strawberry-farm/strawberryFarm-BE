package com.strawberryfarm.fitingle.domain.auth.dto.emailDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmailCertificationResponseDto extends BaseDto {
	private String email;
}
