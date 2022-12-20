package com.github.rbaul.tenantonthefly.mongodb;

import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import com.github.rbaul.tenantonthefly.mvc.TenantManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Slf4j
public class TenantManagementMongoService extends TenantManagementService {
	private final TenantRepository tenantRepository;
	private final MongoTemplate mongoTemplate;
	
	private final MultiTenantMongoClientDatabaseFactory multiTenantMongoClientDatabaseFactory;
	
	private static final String VALID_DATABASE_NAME_REGEXP = "[^/\\\\.$\"\\s]+";
	
	@PostConstruct
	public void initDatabase() {
		createDefaultTenant();
	}
	@Override
	protected void initTenantDatabase(String databaseName) {
		super.initTenantDatabase(databaseName);
		// Create all relevant document (Mongock)
//		mongoTemplate
	}
	
	@Override
	protected void dropTenantDatabase(String databaseName) {
		super.dropTenantDatabase(databaseName);
		mongoTemplate.getDb().drop();
	}
	
	@Override
	protected String getDefaultDatabaseName() {
		return multiTenantMongoClientDatabaseFactory.getDefaultDatabaseName();
	}
}
