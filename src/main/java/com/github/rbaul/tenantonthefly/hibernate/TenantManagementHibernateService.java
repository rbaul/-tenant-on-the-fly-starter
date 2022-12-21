package com.github.rbaul.tenantonthefly.hibernate;

import com.github.rbaul.tenantonthefly.TenantOnTheFlyProperties;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import com.github.rbaul.tenantonthefly.exceptions.TenantOnTheFlyException;
import com.github.rbaul.tenantonthefly.mvc.TenantManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class TenantManagementHibernateService extends TenantManagementService {
	private final DataSource dataSource;
	
	private final SchemaConfigurableMultiTenantConnectionProvider schemaConfigurableMultiTenantConnectionProvider;
	
	private final TenantRepository tenantRepository;
	private final TenantOnTheFlyProperties tenantOnTheFlyProperties;
	
	private static final String VALID_SCHEMA_NAME_REGEXP = "[A-Za-z0-9_]*";
	
	@PostConstruct
	public void initDatabase() {
		ClassicConfiguration classicConfiguration = new ClassicConfiguration();
		classicConfiguration.setDataSource(dataSource);
		classicConfiguration.setLocations(new Location(tenantOnTheFlyProperties.getFlywayLocation()));
		Flyway flyway = new Flyway(classicConfiguration);
		flyway.migrate();
		
		createDefaultTenant();
	}
	
	@Override
	protected void initTenantDatabase(String databaseName) {
		// Verify schema string to prevent SQL injection
		if (!databaseName.matches(VALID_SCHEMA_NAME_REGEXP)) {
			throw new TenantOnTheFlyException("Schema name not valid -> " + databaseName);
		}
		
		try {
			runFlyway(databaseName);
			super.initTenantDatabase(databaseName);
		} catch (Exception e) {
			throw new TenantOnTheFlyException("Error when populating schema: ", e);
		}
	}
	
	@Override
	protected void dropTenantDatabase(String databaseName) {
		try {
			dataSource.getConnection().setSchema(databaseName);
//			String dropDatabase = String.format("DROP SCHEMA %s cascade", database);
			String dropDatabase = String.format("DROP DATABASE %s", databaseName);
			dataSource.getConnection().prepareStatement(dropDatabase).execute();
		} catch (SQLException e) {
			log.error("Failed remove schema name '{}'", databaseName, e);
		}
	}
	
	private void runFlyway(String schema) throws Exception {
		ClassicConfiguration classicConfiguration = new ClassicConfiguration();
		classicConfiguration.setDataSource(dataSource);
		classicConfiguration.setSchemas(new String[]{schema});
//		classicConfiguration.setCreateSchemas(true);
		classicConfiguration.setDefaultSchema(schema);
		Flyway flyway = new Flyway(classicConfiguration);
		
		flyway.migrate();
//		FlywayMigrationStrategy migrationStrategy = flyway1 -> {
//			log.info("FlywayAutoConfiguration:FlywayMigrationStrategy - Start to migrate ...");
////			flyway1.baseline();
////			flyway1.repair();
//			flyway1.migrate();
//		};
//		migrationStrategy.migrate(flyway);
	}
	
	@Override
	protected String getDefaultDatabaseName() {
		return schemaConfigurableMultiTenantConnectionProvider.getDefaultDatabaseName();
	}
}
