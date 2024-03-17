package com.strawberryfarm.fitingle.domain.apply.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class ApplyResponseDto extends BaseDto {
	private String contents;
}
