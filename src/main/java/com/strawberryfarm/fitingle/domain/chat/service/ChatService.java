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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {

	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
	public void enter(ChatMessageDto chatMessageDto,RabbitTemplate rabbitTemplate) {
		chatMessageDto.setMessage("입장하였습니다.");
		chatMessageDto.setRegDate(LocalDateTime.now());
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatMessageDto.getChatRoomId(),chatMessageDto);
	}

	public void send(ChatMessageDto chatMessageDto,RabbitTemplate rabbitTemplate) {
		chatMessageDto.setRegDate(LocalDateTime.now());
		System.out.println(rabbitTemplate.getExchange());
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatMessageDto.getChatRoomId(),chatMessageDto);
	}
}
