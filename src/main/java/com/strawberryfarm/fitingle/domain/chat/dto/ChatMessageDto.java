package com.strawberryfarm.fitingle.domain.chat.dto;

import com.strawberryfarm.fitingle.domain.chat.type.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
	private Long chatId;
	private Long chatRoomId;
	private Long userId;

	private String message;
	private String sender;

	public void modifyMessage(String message) {
		this.message = message;
	}
}
