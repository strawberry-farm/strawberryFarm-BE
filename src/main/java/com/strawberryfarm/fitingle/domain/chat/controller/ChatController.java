package com.strawberryfarm.fitingle.domain.chat.controller;

import com.strawberryfarm.fitingle.domain.chat.dto.ChatMessageDto;
import com.strawberryfarm.fitingle.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final RabbitTemplate rabbitTemplate;

	@MessageMapping("chat.join.{chatRoomId}")
	public void join(
		@AuthenticationPrincipal UserDetails userDetails,
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		Long userId = Long.parseLong(userDetails.getUsername());
		chatService.join(userId,chatMessageDto,rabbitTemplate,chatRoomId);
	}

	@MessageMapping("chat.message.{chatRoomId}")
	public void send(
		@AuthenticationPrincipal UserDetails userDetails,
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		Long userId = Long.parseLong(userDetails.getUsername());
		chatService.messageSend(userId,chatRoomId,chatMessageDto,rabbitTemplate);
	}

	@MessageMapping("chat.exit.{chatRoomId}")
	public void exitChatRoom(
		@AuthenticationPrincipal UserDetails userDetails,
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		Long userId = Long.parseLong(userDetails.getUsername());
		chatService.exitChatRoom(userId,chatRoomId,chatMessageDto,rabbitTemplate);
	}

	@RabbitListener(queues = "chat.queue")
	public void receive(ChatMessageDto chatMessageDto) {
		System.out.println(chatMessageDto.getNickname());
		System.out.println(chatMessageDto.getMessage());
		System.out.println(chatMessageDto.getRegDate());
	}
}
