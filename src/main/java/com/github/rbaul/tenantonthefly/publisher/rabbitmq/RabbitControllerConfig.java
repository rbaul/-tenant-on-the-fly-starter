package com.github.rbaul.tenantonthefly.publisher.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rbaul.tenantonthefly.mvc.NotificationTypeDto;
import com.github.rbaul.tenantonthefly.mvc.TenantDto;
import com.github.rbaul.tenantonthefly.mvc.TenantNotificationDto;
import com.github.rbaul.tenantonthefly.mvc.TenantUpdatePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Conditional(RabbitControllerConfig.RabbitPublisherControllerCondition.class)
@ConditionalOnClass(RabbitTemplate.class)
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RabbitControllerConfig {
	
	public static final String TENANT_TOPIC_EXCHANGE = "tenant-topic";
	
	public static final String TENANT_ROUTING_KEY = "tenant-update";
	
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(TENANT_TOPIC_EXCHANGE);
	}
	
	public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper objectMapper) {
		Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
		jackson2JsonMessageConverter.setAssumeSupportedContentType(false);
		return jackson2JsonMessageConverter;
	}
	
	@Bean
	public TenantUpdatePublisher rabbitTenantPublisher(final ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter(objectMapper));
		
		return new TenantUpdatePublisher() {
			@Override
			public void created(TenantDto tenant) {
				rabbitTemplate.convertAndSend(TENANT_TOPIC_EXCHANGE, TENANT_ROUTING_KEY,
						TenantNotificationDto.builder()
								.notificationType(NotificationTypeDto.OBJECT_CREATION)
								.content(tenant).build());
			}
			
			@Override
			public void deleted(String tenantName) {
				rabbitTemplate.convertAndSend(TENANT_TOPIC_EXCHANGE, TENANT_ROUTING_KEY,
						TenantNotificationDto.builder()
								.notificationType(NotificationTypeDto.OBJECT_DELETION)
								.content(TenantDto.builder()
										.name(tenantName).build()).build());
			}
		};
	}
	
	static class RabbitPublisherControllerCondition extends AllNestedConditions {
		
		RabbitPublisherControllerCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}
		
		@ConditionalOnProperty(name = "tenant.on-the-fly.controller", havingValue = "true")
		static class ControllerCondition {
		}
		
		@ConditionalOnProperty(name = "tenant.on-the-fly.notifier-type", havingValue = "RABBIT_MQ")
		static class RabbitPublisherCondition {
		}
	}
}
