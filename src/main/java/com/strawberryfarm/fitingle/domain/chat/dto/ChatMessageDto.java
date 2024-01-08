package com.strawberryfarm.fitingle.domain.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.strawberryfarm.fitingle.domain.chat.type.ChatType;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@Getter
public class ChatMessageDto extends BaseDto {
	private String nickname;

	private String message;

	private String accessToken;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime regDate;

	public void modifyMessage(String message) {
		this.message = message;
	}

	public void modifyRegDate(LocalDateTime regDate) {
		this.regDate = regDate;
	}
}
