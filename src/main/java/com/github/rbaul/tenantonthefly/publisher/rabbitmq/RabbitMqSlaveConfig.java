package com.github.rbaul.tenantonthefly.publisher.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rbaul.tenantonthefly.mvc.NotificationTypeDto;
import com.github.rbaul.tenantonthefly.mvc.TenantManagementService;
import com.github.rbaul.tenantonthefly.mvc.TenantNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Conditional(RabbitMqSlaveConfig.RabbitConsumerSlaveCondition.class)
@ConditionalOnClass(RabbitTemplate.class)
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RabbitMqSlaveConfig {
	
	private final TenantManagementService tenantManagementService;
	
	@Autowired
	public void rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory, ObjectMapper objectMapper) {
		rabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
	}
	
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(name = RabbitControllerConfig.TENANT_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
			key = RabbitControllerConfig.TENANT_ROUTING_KEY,
			value = @Queue(value = "", durable = "true")))
	public void receiveTenantUpdate(TenantNotificationDto tenantNotificationDto) {
		log.info(">>> Received Tenant update notification: '{}'", tenantNotificationDto);
		NotificationTypeDto notificationType = tenantNotificationDto.getNotificationType();
		switch (notificationType) {
			case OBJECT_CREATION -> tenantManagementService.create(tenantNotificationDto.getContent());
			case OBJECT_DELETION -> tenantManagementService.delete(tenantNotificationDto.getContent().getName());
		}
	}
	
	static class RabbitConsumerSlaveCondition extends AllNestedConditions {
		
		RabbitConsumerSlaveCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}
		
		@ConditionalOnProperty(name = "tenant.on-the-fly.controller", havingValue = "false", matchIfMissing = true)
		static class ControllerCondition {
		}
		
		@ConditionalOnProperty(name = "tenant.on-the-fly.notifier-type", havingValue = "RABBIT_MQ")
		static class RabbitPublisherCondition {
		}
	}
	
}
