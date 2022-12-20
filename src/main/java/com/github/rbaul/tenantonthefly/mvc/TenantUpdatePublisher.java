package com.github.rbaul.tenantonthefly.mvc;

public interface TenantUpdatePublisher {
	void created(TenantDto tenant);
	
	void deleted(String tenantName);
}
