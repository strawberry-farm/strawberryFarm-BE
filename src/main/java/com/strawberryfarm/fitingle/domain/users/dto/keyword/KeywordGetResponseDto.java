package com.strawberryfarm.fitingle.domain.users.dto.keyword;

import com.strawberryfarm.fitingle.domain.keyword.dto.KeywordDto;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KeywordGetResponseDto extends BaseDto {
	private List<KeywordDto> keywords;
}
