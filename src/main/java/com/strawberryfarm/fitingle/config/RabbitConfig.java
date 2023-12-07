package com.strawberryfarm.fitingle.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.*;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
	private static final String CHAT_QUEUE_NAME = "chat.queue";
	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
	private static final String ROUTING_KEY = "room.*";

	//rabbitmq에 날릴 command
	// 큐들을 생성
	//각 큐들의이름은 chat.queue가 된다.
	@Bean
	public Queue queue() {
		return new Queue(CHAT_QUEUE_NAME,true);
	}

	//rabbitmq에 날릴command
	//구독하기
	//구독을 하기 위한 명령이 날아가는 것 같음
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(CHAT_EXCHANGE_NAME);
	}

	//큐와 exchange 바인딩
	@Bean
	public Binding binding(Queue queue,TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}

	//???
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("guest");
		factory.setPassword("guest");
		return factory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setRoutingKey(ROUTING_KEY);
		return rabbitTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer container() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(CHAT_QUEUE_NAME);
		container.setMessageListener(null);
		return container;
	}
}
