package com.strawberryfarm.fitingle.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatErrorMessageDto {
	private String errorCode;
	private String message;
}
