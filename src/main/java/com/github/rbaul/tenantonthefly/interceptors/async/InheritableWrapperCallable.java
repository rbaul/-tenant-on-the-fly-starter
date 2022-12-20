package com.github.rbaul.tenantonthefly.interceptors.async;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
@ToString
public class InheritableWrapperCallable<V> implements Callable<V> {
	
	private final Tenant tenant;
	private final Callable<V> callable;
	
	public InheritableWrapperCallable(final Callable<V> callable) {
		this.callable = callable;
		this.tenant = TenantContext.getCurrentTenant();
	}
	
	@Override
	public V call() throws Exception {
		try {
			// set the context of this thread to that of its parent
			TenantContext.setCurrentTenant(tenant);
			log.debug("Inheritance thread propagation tenant id '{}'", TenantContext.getCurrentTenant());
			return callable.call();
		} finally {
			// Clear context
			TenantContext.clear();
		}
	}
}