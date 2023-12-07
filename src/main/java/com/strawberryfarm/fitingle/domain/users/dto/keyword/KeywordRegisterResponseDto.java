package com.strawberryfarm.fitingle.domain.users.dto.keyword;

import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRegisterResponseDto extends BaseDto {
	private List<String> keywords = new ArrayList<>();
}
