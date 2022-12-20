package com.github.rbaul.tenantonthefly.interceptors;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
//@ConditionalOnBean(WebRequestHandlerInterceptorAdapter.class)
@Slf4j
@Component
@Order(1)
class TenantRestFilter implements Filter {
	
	private final TenantRepository tenantRepository;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {
		
		String tenantsHeader = ((HttpServletRequest) request).getHeader(TenantContext.TENANT_HEADER);
		log.info("****Received tenant IDs in header: " + tenantsHeader);
		if (StringUtils.hasText(tenantsHeader)) {
			Optional<Tenant> tenantOptional = tenantRepository.findById(tenantsHeader);
			tenantOptional.ifPresent(TenantContext::setCurrentTenant);
		}
		try {
			chain.doFilter(request, response);
		} finally {
			TenantContext.clear();
		}
		
	}
}