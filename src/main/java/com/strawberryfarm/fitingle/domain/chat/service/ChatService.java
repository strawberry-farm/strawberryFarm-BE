package com.strawberryfarm.fitingle.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatCreateResponseDto;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatErrorMessageDto;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatListResponseDto;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatListResponseDto.ChatInfo;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatMessageDto;
import com.strawberryfarm.fitingle.domain.chat.entity.Chat;
import com.strawberryfarm.fitingle.domain.chat.repository.ChatRepository;
import com.strawberryfarm.fitingle.domain.chatRoom.entity.ChatRoom;
import com.strawberryfarm.fitingle.domain.chatRoom.repository.ChatRoomRepository;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRepository chatRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final RabbitAdmin rabbitAdmin;
	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
	public void join(Long userId, ChatMessageDto chatMessageDto,RabbitTemplate rabbitTemplate,Long chatRoomId) {
		//message 커스터 마이징
		chatMessageDto.modifyMessage(chatMessageDto.getNickname() + "님이 입장하였습니다.");
		chatMessageDto.modifyRegDate(LocalDateTime.now());

		//채팅방 존재 여부 확인
		Optional<ChatRoom> findChatRooms = chatRoomRepository.findById(chatRoomId);

		//채팅방이 없을 시, 에러 메시지를 보냄
		if (!findChatRooms.isPresent()) {
			exceptSend(userId,rabbitTemplate,ErrorCode.NOT_EXIST_CHAT_ROOM);
		}

		//입장 메시지 보내기
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatRoomId,chatMessageDto);

		//채팅과 채팅방 db에 저장
		ChatRoom findChatRoom = findChatRooms.get();
		Chat newChat = Chat.builder()
			.nicName(chatMessageDto.getNickname())
			.message(chatMessageDto.getMessage())
			.build();
		newChat.modifyChatroom(findChatRoom);

		chatRepository.save(newChat);
	}

	public void messageSend(Long userId,Long chatRoomId,ChatMessageDto chatMessageDto,RabbitTemplate rabbitTemplate) {
		//메시지 커스터 마이징
		chatMessageDto.modifyRegDate(LocalDateTime.now());

		//채팅방 존재 여부 확인
		Optional<ChatRoom> findChatRooms = chatRoomRepository.findById(chatRoomId);

		//채팅방이 없을 시, 에러 메시지를 보냄
		if (!findChatRooms.isPresent()) {
			exceptSend(userId,rabbitTemplate,ErrorCode.NOT_EXIST_CHAT_ROOM);
		}

		//메시지 보내기
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatRoomId,chatMessageDto);

		//채팅과 채팅방 db에 저장
		ChatRoom findChatRoom = findChatRooms.get();
		Chat newChat = Chat.builder()
			.message(chatMessageDto.getMessage())
			.build();
		newChat.modifyChatroom(findChatRoom);

		chatRepository.save(newChat);
	}

	public void exitChatRoom(Long userId, Long chatRoomId, ChatMessageDto chatMessageDto, RabbitTemplate rabbitTemplate) {
		//메시지 커스터 마이징
		chatMessageDto.modifyMessage(chatMessageDto.getNickname() + "님이 나가셨습니다.");
		chatMessageDto.modifyRegDate(LocalDateTime.now());

		//채팅방 존재 여부 확인
		Optional<ChatRoom> findChatRooms = chatRoomRepository.findById(chatRoomId);

		//채팅방이 없을 시, 에러 메시지를 보냄
		if (!findChatRooms.isPresent()) {
			exceptSend(userId,rabbitTemplate,ErrorCode.NOT_EXIST_CHAT_ROOM);
		}

		//메시지 보내기
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatRoomId,chatMessageDto);

		//만약, 현재 채팅방에 남은 인원이 없으면 채팅방 삭제
		ChatRoom findChatRoom = findChatRooms.get();
		findChatRoom.reduceNumOfUsers();

		if (findChatRoom.getNumsOfUsers() == 0) {
			chatRoomRepository.delete(findChatRoom);
		}
	}

	public void exceptSend(Long userId, RabbitTemplate rabbitTemplate,ErrorCode errorCode) {
		//에러 메시지 생성
		ChatErrorMessageDto errorMessageDto = ChatErrorMessageDto.builder()
			.message(errorCode.getMessage())
			.errorCode(errorCode.getCode())
			.build();

		//에러 발생 시 동적으로 큐 생성, 여기서는 default로 생성 되어있는 exchange에
		//바인딩 되는 큐를 생성한다. user.queue.userId
		String dynamicQueueName = "user.queue." + userId;

		//동적으로 큐 생성
		Queue dynamicQueue = new Queue(dynamicQueueName,true);

		//큐를 등록
		rabbitAdmin.declareQueue(dynamicQueue);

		//에러 메시지 보내기
		//DB 저장 하지 않는다.
		rabbitTemplate.convertAndSend("",dynamicQueueName,errorMessageDto);
	}

	//마지막 읽은 메시지 10개 + 읽지 않은 메시지 전부
	public ResultDto<?> getChatList(String userId,Long chatRoomId) {
		Optional<ChatRoom> findChatRooms = chatRoomRepository.findById(chatRoomId);

		if (!findChatRooms.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_CHAT_ROOM.getMessage())
				.errorCode(ErrorCode.NOT_EXIST_CHAT_ROOM.getCode())
				.data(null)
				.build();
		}

		ChatRoom findChatRoom = findChatRooms.get();
		String chatRoomSummaryInfo = findChatRoom.getChatRoomSummaryInfo();
		StringBuilder newChatRoomSummaryInfo = new StringBuilder();

		String[] split = chatRoomSummaryInfo.split("|");
		List<ChatInfo> chatInfoList = new ArrayList<>();

		for (int i = 0; i < split.length; i++) {
			StringTokenizer st = new StringTokenizer(split[i]);
			String userIdValue = st.nextToken();

			if (userId.equals(userIdValue)) {
				Long idx = Long.parseLong(st.nextToken());
				List<Chat> chats = chatRepository.findChats(idx - 10L);

				chatInfoList = ChatListToChatInfoList(chats);

				split[i] = userIdValue + " " + (idx + chats.size() - 10);
			}

			if (i == split.length - 1) {
				newChatRoomSummaryInfo.append(split[i]);
			} else {
				newChatRoomSummaryInfo.append(split[i]+"|");
			}
		}

		findChatRoom.modifyChatRoomSummaryInfo(newChatRoomSummaryInfo.toString());

		return ChatListResponseDto.builder()
			.chatList(chatInfoList)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
	}

	private List<ChatInfo> ChatListToChatInfoList(List<Chat> chats) {
		List<ChatInfo> result = new ArrayList<>();

		for(Chat chat: chats) {
			result.add(ChatInfo.builder()
					.nickName(chat.getNicName())
					.message(chat.getMessage())
					.time(chat.getCreatedDate())
				.build());
		}
		return result;
	}
}
