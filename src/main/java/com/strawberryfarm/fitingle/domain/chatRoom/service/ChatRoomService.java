package com.strawberryfarm.fitingle.domain.chatRoom.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatListResponseDto;
import com.strawberryfarm.fitingle.domain.chatRoom.dto.ChatRoomsDto;
import com.strawberryfarm.fitingle.domain.chatRoom.dto.ChatRoomsListResponseDto;
import com.strawberryfarm.fitingle.domain.userchatroom.entity.UsersChatRoom;
import com.strawberryfarm.fitingle.domain.chatRoom.entity.ChatRoom;
import com.strawberryfarm.fitingle.domain.chatRoom.repository.ChatRoomRepository;
import com.strawberryfarm.fitingle.domain.userchatroom.repository.UsersChatRoomRepository;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final UsersRepository usersRepository;
	private final UsersChatRoomRepository usersChatRoomRepository;

	public ChatRoom createChatRoom(Long userId, String chatRoomName) {
		ChatRoom newChatRoom = ChatRoom.builder()
			.numsOfUsers(1)
			.chatRoomSummaryInfo(userId + " " + 0)
			.chatRoomName(chatRoomName)
			.build();

		UsersChatRoom usersChatRoom = UsersChatRoom.builder().build();

		Users findUsers = usersRepository.findUsersById(userId);

		if (findUsers == null) {
			return null;
		}

		usersChatRoom.modifyUsers(findUsers);
		usersChatRoom.modifyChatRoom(newChatRoom);

		ChatRoom result = chatRoomRepository.save(newChatRoom);
		usersChatRoomRepository.save(usersChatRoom);

		return result;
	}

	public void createChatRoom() {
		ChatRoom newChatRoom = ChatRoom.builder()
			.chatRoomName("test")
			.numsOfUsers(1)
			.build();

		chatRoomRepository.save(newChatRoom);
	}

	public ResultDto<?> getChatRooms(Long userId) {
		Optional<Users> findUsers = usersRepository.findById(userId);

		if (!findUsers.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Users findUser = findUsers.get();
		List<UsersChatRoom> usersChatRooms = findUser.getUsersChatRooms();

		List<ChatRoomsDto> chatRoomsDtoList = new ArrayList<>();
		for (UsersChatRoom usersChatRoom : usersChatRooms) {
			ChatRoom chatRoom = usersChatRoom.getChatRoom();

			chatRoomsDtoList.add(ChatRoomsDto.builder()
					.chatRoomName(chatRoom.getChatRoomName())
					.numOfUsers(chatRoom.getNumsOfUsers())
				.build());
		}

		return ChatRoomsListResponseDto.builder()
			.chatRooms(chatRoomsDtoList)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}
}
