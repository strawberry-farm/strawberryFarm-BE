package com.strawberryfarm.fitingle.domain.chat.controller;

import com.strawberryfarm.fitingle.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRestController {
	private final ChatService chatService;
	@GetMapping("/chat/list/{chatRoomId}")
	public ResponseEntity<?> getChats(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long chatRoomId) {
		String userId = userDetails.getUsername();
		return ResponseEntity.ok(chatService.getChatList(userId,chatRoomId));
	}
}
