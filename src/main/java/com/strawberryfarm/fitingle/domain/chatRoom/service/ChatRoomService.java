package com.strawberryfarm.fitingle.domain.chatRoom.service;

import com.strawberryfarm.fitingle.domain.userchatroom.entity.UsersChatRoom;
import com.strawberryfarm.fitingle.domain.chatRoom.entity.ChatRoom;
import com.strawberryfarm.fitingle.domain.chatRoom.repository.ChatRoomRepository;
import com.strawberryfarm.fitingle.domain.userchatroom.repository.UsersChatRoomRepository;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final UsersRepository usersRepository;
	private final UsersChatRoomRepository usersChatRoomRepository;

	public ChatRoom createChatRoom(Long userId) {
		ChatRoom newChatRoom = ChatRoom.builder().build();
		UsersChatRoom usersChatRoom = UsersChatRoom.builder().build();

		Users findUsers = usersRepository.findUsersById(userId);

		usersChatRoom.modifyUsers(findUsers);
		usersChatRoom.modifyChatRoom(newChatRoom);

		ChatRoom result = chatRoomRepository.save(newChatRoom);
		usersChatRoomRepository.save(usersChatRoom);

		return result;
	}

	public ResultDto<?> getChatRooms(Long userId) {

		return ResultDto.builder().build();
	}
}
