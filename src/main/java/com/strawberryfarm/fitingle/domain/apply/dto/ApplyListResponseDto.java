package com.strawberryfarm.fitingle.domain.apply.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplyListResponseDto extends BaseDto {
	List<ApplyDto> applyList;
}
