package com.strawberryfarm.fitingle.domain.chat.controller;

import com.strawberryfarm.fitingle.domain.chat.dto.ChatMessageDto;
import com.strawberryfarm.fitingle.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final RabbitTemplate rabbitTemplate;

	@MessageMapping("chat.join.{chatRoomId}")
	public void join(
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		chatService.join(chatMessageDto,rabbitTemplate,chatRoomId);
	}

	@MessageMapping("chat.message.{chatRoomId}")
	public void send(
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		chatService.messageSend(chatRoomId,chatMessageDto,rabbitTemplate);
	}

	@MessageMapping("chat.exit.{chatRoomId}")
	public void exitChatRoom(
		@Payload ChatMessageDto chatMessageDto,
		@DestinationVariable Long chatRoomId) {
		chatService.exitChatRoom(chatRoomId,chatMessageDto,rabbitTemplate);
	}

	@RabbitListener(queues = "chat.queue")
	public void receive(ChatMessageDto chatMessageDto) {
		System.out.println(chatMessageDto.getNickname());
		System.out.println(chatMessageDto.getMessage());
		System.out.println(chatMessageDto.getRegDate());
	}
}
