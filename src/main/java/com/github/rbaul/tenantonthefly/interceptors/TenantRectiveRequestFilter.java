package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@ConditionalOnBean(ReactiveAdapter.class)
@RequiredArgsConstructor
@Slf4j
@Component
@Order(1)
class TenantRectiveRequestFilter implements WebFilter {
	
	private final TenantRepository tenantRepository;
	
	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange,
							 WebFilterChain webFilterChain) {
		ServerHttpRequest request = serverWebExchange.getRequest();
		HttpHeaders headers = request.getHeaders();
		List<String> tenantHeader = headers.get(TenantContext.TENANT_HEADER);
		log.info("****Received tenant IDs in header: " + tenantHeader);
		if (!CollectionUtils.isEmpty(tenantHeader)) {
			Optional<Tenant> tenantOptional = tenantRepository.findById(tenantHeader.get(0));
			tenantOptional.ifPresent(TenantContext::setCurrentTenant);
		}
		return webFilterChain.filter(serverWebExchange).doFinally(signalType -> TenantContext.clear());
	}
}