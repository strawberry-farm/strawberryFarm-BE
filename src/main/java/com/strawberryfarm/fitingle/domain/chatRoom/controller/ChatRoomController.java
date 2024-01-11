package com.strawberryfarm.fitingle.domain.chatRoom.controller;

import com.strawberryfarm.fitingle.domain.chatRoom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
	private final ChatRoomService chatRoomService;

	@GetMapping("/chatRoom")
	public ResponseEntity<?> getChatRooms(@AuthenticationPrincipal UserDetails userDetails) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(chatRoomService.getChatRooms(userId));
	}

	@PostMapping("/chatRoom/create")
	public ResponseEntity<?> createChatRoom() {
		chatRoomService.createChatRoom(1L,"123123");
		return ResponseEntity.ok("aaaa");
	}
}
