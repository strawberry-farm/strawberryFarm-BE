package com.strawberryfarm.fitingle.domain.users.dto.keyword;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDeleteResponseDto extends BaseDto {
	private String email;
}
