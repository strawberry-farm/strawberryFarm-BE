package com.strawberryfarm.fitingle.domain.chat.controller;

import com.strawberryfarm.fitingle.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@PostMapping
	public ResponseEntity<?> createChat() {
		return ResponseEntity.ok(chatService.createChatRoom());
	}
}
