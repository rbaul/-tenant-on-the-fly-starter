package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

@ConditionalOnClass(SimpleRabbitListenerContainerFactory.class)
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitListenerContainerBeanPostProcessor implements BeanPostProcessor {
	
	private final TenantRepository tenantRepository;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof SimpleRabbitListenerContainerFactory) {
			log.info("Add interceptor to SimpleRabbitListenerContainerFactory: '{}'", beanName);
			SimpleRabbitListenerContainerFactory containerFactory = (SimpleRabbitListenerContainerFactory) bean;
			containerFactory.setAdviceChain((MethodInterceptor) invocation -> {
				Message message = (Message) invocation.getArguments()[1];
				String tenantName = (String) message.getMessageProperties().getHeaders().get(TenantContext.TENANT_HEADER);
				
				try {
					if (StringUtils.hasText(tenantName)) {
						Optional<Tenant> tenantOptional = tenantRepository.findById(tenantName);
						tenantOptional.ifPresent(TenantContext::setCurrentTenant);
					}
					return invocation.proceed();
				} finally {
					TenantContext.clear();
				}
			});
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
