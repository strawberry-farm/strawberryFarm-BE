package com.strawberryfarm.fitingle.domain.chat.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class ChatCreateResponseDto extends BaseDto {
	private Long chatId;
}
