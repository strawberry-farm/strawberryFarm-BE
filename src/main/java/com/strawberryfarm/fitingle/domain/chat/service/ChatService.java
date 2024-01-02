package com.strawberryfarm.fitingle.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatCreateResponseDto;
import com.strawberryfarm.fitingle.domain.chat.dto.ChatMessageDto;
import com.strawberryfarm.fitingle.domain.chat.entity.Chat;
import com.strawberryfarm.fitingle.domain.chat.repository.ChatRepository;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ObjectMapper objectMapper;
	private final UsersRepository usersRepository;
	private final ChatRepository chatRepository;

	public ResultDto<?> createChatRoom() {
		Chat newChat = Chat.builder().build();
		Chat createdChat = chatRepository.save(newChat);


		return ChatCreateResponseDto.builder()
			.chatId(createdChat.getId())
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> joinChatRoom() {
		return ResultDto.builder().build();
	}

	public <T> void sendMessage(WebSocketSession session, T message) {
		try {
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ResultDto<?> outChat(long chatId, ApplicationContext context) {
		return ResultDto.builder().build();
	}
}
