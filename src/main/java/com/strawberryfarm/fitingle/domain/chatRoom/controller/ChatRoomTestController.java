package com.strawberryfarm.fitingle.domain.chatRoom.controller;

import javax.persistence.GeneratedValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/chat")
public class ChatRoomTestController {

	@GetMapping(value = "/room")
	public String getRoom(Long chatRoomId, String nickname, Model model){

		model.addAttribute("chatRoomId", chatRoomId);
		model.addAttribute("nickname", nickname);

		return "chat/room";
	}
}
