package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.interceptors.async.InheritableExecutorDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

//@Component
@Slf4j
@RequiredArgsConstructor
public class ExecutorBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Executor) {
			log.info("Add delegate to Executor: '{}'", beanName);
			Executor executor = (Executor) bean;
			return BeanPostProcessor.super.postProcessAfterInitialization(new InheritableExecutorDelegate(executor), beanName);
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
