package com.github.rbaul.tenantonthefly.interceptors.async;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class InheritableWrapperRunnable implements Runnable {
	
	private final Tenant tenant;
	private final Runnable runnable;
	
	public InheritableWrapperRunnable(final Runnable runnable) {
		this.runnable = runnable;
		this.tenant = TenantContext.getCurrentTenant();
	}
	
	@Override
	public void run() {
		try {
			// set the context of this thread to that of its parent
			TenantContext.setCurrentTenant(tenant);
			runnable.run();
		} finally {
			// Clear context
			TenantContext.clear();
		}
	}
}