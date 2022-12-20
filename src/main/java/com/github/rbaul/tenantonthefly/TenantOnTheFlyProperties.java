package com.github.rbaul.tenantonthefly;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("tenant.on-the-fly")
public class TenantOnTheFlyProperties {
	/**
	 * Location of the Flyway script for tenant on the fly
	 */
	private String flywayLocation = "tenant_db";
	
	/**
	 * This master microservice
	 */
	private boolean controller = false;
	
	/**
	 * Notifier type between microservices
	 */
	private TenantOnTheFlyNotifierType notifierType = TenantOnTheFlyNotifierType.NONE;
}
