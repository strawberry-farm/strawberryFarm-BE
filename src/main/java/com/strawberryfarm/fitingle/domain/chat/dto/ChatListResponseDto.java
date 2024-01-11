package com.strawberryfarm.fitingle.domain.chat.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatListResponseDto extends BaseDto {
	private List<ChatInfo> chatList;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatInfo {
		private String nickName;
		private String message;

		private LocalDateTime time;
	}
}
