package com.strawberryfarm.fitingle.config;


import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
	private static final String CHAT_QUEUE_NAME = "chat.queue";
	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
	private static final String ROUTING_KEY = "room.*";

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	//Exchange 등록
	@Bean
	public TopicExchange exchange(){ return new TopicExchange(CHAT_EXCHANGE_NAME); }

	@Bean
	public Queue queue() {
		return new Queue(CHAT_QUEUE_NAME, true);
	}

	@Bean
	public Binding binding(TopicExchange exchange, Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	@Bean public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

//	@Bean
//	public SimpleMessageListenerContainer container(){
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory());
//		container.setQueueNames("chat.room.1.queue");
//		return container;
//	}

	//Spring에서 자동생성해주는 ConnectionFactory는 SimpleConnectionFactory인가? 그건데
	//여기서 사용하는 건 CachingConnectionFacotry라 새로 등록해줌
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(host);
		factory.setUsername(username);
		factory.setPassword(password);
		return factory;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter(){
		//LocalDateTime serializable을 위해
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		objectMapper.registerModule(dateTimeModule());

		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

		return converter;
	}

	@Bean
	public Module dateTimeModule(){
		return new JavaTimeModule();
	}
}
