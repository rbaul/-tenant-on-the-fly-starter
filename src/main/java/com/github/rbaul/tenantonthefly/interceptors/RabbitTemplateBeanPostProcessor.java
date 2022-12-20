package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@ConditionalOnClass(RabbitTemplate.class)
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitTemplateBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof RabbitTemplate) {
			log.info("Add interceptor to RabbitTemplate: '{}'", beanName);
			RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
			rabbitTemplate.addBeforePublishPostProcessors(getMessagePostProcessor());
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
	
	/**
	 * Creates a new {@link MessagePostProcessor} that will add the ID to the
	 * message header.
	 *
	 * @return the created {@link MessagePostProcessor}.
	 */
	private MessagePostProcessor getMessagePostProcessor() {
		
		return message -> {
			Tenant currentTenant = TenantContext.getCurrentTenant();
			if (currentTenant != null) {
				message.getMessageProperties().setHeader(TenantContext.TENANT_HEADER, currentTenant.getName());
			}
			return message;
		};
	}
}
