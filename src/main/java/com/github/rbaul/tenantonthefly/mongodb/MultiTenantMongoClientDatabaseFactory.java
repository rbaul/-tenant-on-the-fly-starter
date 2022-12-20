package com.github.rbaul.tenantonthefly.mongodb;

import com.github.rbaul.tenantonthefly.TenantContext;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.mongodb.client.MongoClient;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.StringUtils;

public class MultiTenantMongoClientDatabaseFactory extends SimpleMongoClientDatabaseFactory {
	
	public MultiTenantMongoClientDatabaseFactory(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
	}
	
	@Override
	protected String getDefaultDatabaseName() {
		Tenant tenant = TenantContext.getCurrentTenant();
		return tenant != null ? tenant.getDatabaseName() : super.getDefaultDatabaseName();
	}
	
}
