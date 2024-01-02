package com.strawberryfarm.fitingle.domain.chat.controller;

import com.strawberryfarm.fitingle.domain.chat.dto.ChatMessageDto;
import com.strawberryfarm.fitingle.domain.chat.service.ChatService;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final RabbitTemplate rabbitTemplate;

	@MessageMapping("chat.enter.{chatRoomId}")
	public void enter(@Payload ChatMessageDto chatMessageDto,@DestinationVariable Long chatRoomId) {
		chatService.enter(chatMessageDto,rabbitTemplate);
	}

	@MessageMapping("chat.message.{chatRoomId}")
	public void send(@Payload ChatMessageDto chatMessageDto, @DestinationVariable Long chatRoomId) {
		chatService.send(chatMessageDto,rabbitTemplate);
	}

	@RabbitListener(queues = "chat.queue")
	public void receive(ChatMessageDto chatMessageDto) {
		System.out.println("received : " + chatMessageDto.getNickname());
		System.out.println("id : " + chatMessageDto.getChatRoomId());
		System.out.println("message : " + chatMessageDto.getMessage());
	}
}
