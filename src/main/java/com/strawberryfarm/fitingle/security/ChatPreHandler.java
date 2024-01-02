package com.strawberryfarm.fitingle.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {
	private final JwtTokenManager jwtTokenManager;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		try {
			StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);


			//연결 및 구독 확인

		}catch (Exception e) {

		}

		return message;
	}
}
