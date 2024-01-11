package com.strawberryfarm.fitingle.domain.chatRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomsDto {
	private String chatRoomName;
	private int numOfUsers;
}
