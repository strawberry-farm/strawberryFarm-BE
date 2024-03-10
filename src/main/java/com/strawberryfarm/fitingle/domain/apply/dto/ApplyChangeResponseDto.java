package com.strawberryfarm.fitingle.domain.apply.dto;

import com.strawberryfarm.fitingle.domain.apply.entity.ApplyStatus;
import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplyChangeResponseDto extends BaseDto {
	private ApplyStatus beforeStatus;
	private ApplyStatus curStatus;
}
