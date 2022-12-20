package com.github.rbaul.tenantonthefly.hibernate;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import java.util.Map;

@RequiredArgsConstructor
class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
	
	public static String defaultTenant = "unknown";
	
	@Override
	public String resolveCurrentTenantIdentifier() {
		Tenant tenant = TenantContext.getCurrentTenant();
		return tenant != null ? tenant.getDatabaseName() : defaultTenant;
	}
	
	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}
	
	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
	}
	
	// empty overrides skipped for brevity
}