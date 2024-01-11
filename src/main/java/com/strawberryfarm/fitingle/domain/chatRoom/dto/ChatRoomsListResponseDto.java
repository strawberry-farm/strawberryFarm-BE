package com.strawberryfarm.fitingle.domain.chatRoom.dto;

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
public class ChatRoomsListResponseDto extends BaseDto {
	private List<ChatRoomsDto> chatRooms;
}
