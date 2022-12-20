package com.github.rbaul.tenantonthefly.mvc;


import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MultiTenantExecuteService {
	
	private final TenantRepository tenantRepository;
	
	/**
	 * Get all tenants
	 */
	public List<Tenant> getAllTenants() {
		return tenantRepository.findAll();
	}
	
	public void executeForAllTenants(Runnable runnable) {
		getAllTenants().forEach(tenant -> {
			execute(tenant, runnable);
		});
	}
	
	public void execute(Tenant tenant, Runnable runnable) {
		TenantContext.setCurrentTenant(tenant);
		runnable.run();
		TenantContext.clear();
	}
}
