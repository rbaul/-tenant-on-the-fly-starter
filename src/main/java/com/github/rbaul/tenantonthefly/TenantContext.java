package com.github.rbaul.tenantonthefly;

import com.github.rbaul.tenantonthefly.domain.model.Tenant;

public class TenantContext {
	
	public static final String TENANT_HEADER = "X-TenantID";
	
	private static InheritableThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();
	
	public static Tenant getCurrentTenant() {
		return currentTenant.get();
	}
	
	public static void setCurrentTenant(Tenant tenant) {
		currentTenant.set(tenant);
	}
	
	public static void clear() {
		currentTenant.set(null);
	}
	
}