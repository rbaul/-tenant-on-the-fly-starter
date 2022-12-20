package com.github.rbaul.tenantonthefly.interceptors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestTemplateBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof RestTemplate) {
			log.info("Add interceptor to RestTemplate: '{}'", beanName);
			RestTemplate restTemplate = (RestTemplate) bean;
			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(
					restTemplate.getInterceptors());
			interceptors.add(new RestTemplateInterceptor());
			restTemplate.setInterceptors(interceptors);
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
