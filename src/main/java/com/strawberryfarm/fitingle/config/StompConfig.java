package com.strawberryfarm.fitingle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/chat") // 소켓 연결 경로
			.setAllowedOriginPatterns("*") // 소켓의 cors 설정
			.withSockJS(); // 소켓을 지원하지 않는 브라우저를 위해
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// url을 chat/room/3 -> chat.room.3으로 변경
		// 보통 .으로 구조를 나타낸다고한다
		registry.setPathMatcher(new AntPathMatcher("."));

		//메시지 송신 경로
		registry.setApplicationDestinationPrefixes("/pub");

		registry.enableStompBrokerRelay("/topic");
	}
}
