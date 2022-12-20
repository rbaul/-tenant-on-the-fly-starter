package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
		HttpHeaders newHeaders = new HttpHeaders();
		
		Tenant currentTenant = TenantContext.getCurrentTenant();
		if (currentTenant != null) {
			Map<String, String> relevantAdditionalHeaders = Map.of(TenantContext.TENANT_HEADER, currentTenant.getName());
			relevantAdditionalHeaders.forEach(newHeaders::add);
			log.debug("Inheritable request '{}', additional headers '{}'", request.getURI(), relevantAdditionalHeaders);
		}
		
		requestWrapper.getHeaders().putAll(newHeaders);
		return execution.execute(requestWrapper, body);
	}
	
}