package com.github.rbaul.tenantonthefly.mvc;

import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantOnTheFlyConverter {
	
	/**
	 * Tenant -> TenantDTO
	 */
	public TenantDto tenantToTenantDto(Tenant tenant) {
		return TenantDto.builder()
				.name(tenant.getName())
				.description(tenant.getDescription())
				.build();
	}
	
	/**
	 * TenantDTO -> Tenant
	 */
	public Tenant tenantDtoToTenant(TenantDto tenantDto) {
		return Tenant.builder()
				.name(tenantDto.getName())
				.description(tenantDto.getDescription())
				.build();
	}
}
