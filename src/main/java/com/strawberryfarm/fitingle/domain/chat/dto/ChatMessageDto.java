package com.strawberryfarm.fitingle.domain.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.strawberryfarm.fitingle.domain.chat.type.ChatType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessageDto {
	private Long id;
	private Long chatRoomId;
	private Long memberId;
	private String nickname;

	private String message;
	private String region;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime regDate;
}
