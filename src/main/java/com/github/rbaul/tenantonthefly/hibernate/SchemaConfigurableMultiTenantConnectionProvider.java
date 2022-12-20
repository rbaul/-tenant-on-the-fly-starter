package com.github.rbaul.tenantonthefly.hibernate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SchemaConfigurableMultiTenantConnectionProvider
		implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
	private final DataSource dataSource;
	
	private final HibernateProperties hibernateProperties;
	
	private String defaultDatabaseName;

//	@Override
//	protected DataSource selectAnyDataSource() {
//		return dataSource;
//	}
//
//	@Override
//	protected DataSource selectDataSource(String tenantIdentifier) {
//		Connection connection = dataSource.getConnection();
//		connection.setSchema(tenantIdentifier);
//		return dataSource;
//	}
	
	@PostConstruct
	public void init() {
		try {
			this.defaultDatabaseName = this.dataSource.getConnection().getCatalog();
		} catch (SQLException e) {
			log.error("Failed read default database name", e);
		}
	}
	
	@Override
	public Connection getAnyConnection() throws SQLException {
		return getConnection(TenantIdentifierResolver.defaultTenant);
	}
	
	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}
	
	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		Connection connection = dataSource.getConnection();
		if (!tenantIdentifier.equals(TenantIdentifierResolver.defaultTenant)) {
			setSchema(connection, tenantIdentifier);
		} else {
			setSchema(connection, defaultDatabaseName);
		}
		return connection;
	}
	
	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		if (!tenantIdentifier.equals(TenantIdentifierResolver.defaultTenant)) {
			setSchema(connection, tenantIdentifier);
		} else {
//			connection.setSchema("PUBLIC");
			setSchema(connection, defaultDatabaseName);
		}
		connection.close();
	}
	
	protected void setSchema(Connection connection, String schemaName) {
		try {
			connection.setCatalog(schemaName);
//			String dropDatabase = String.format("USE %s", schemaName);
//			connection.prepareStatement(dropDatabase).execute();
		} catch (SQLException e) {
			log.error("Failed change schema to '{}'", schemaName, e);
		}
	}
	
	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}
	
	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
		hibernateProperties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
//		hibernateProperties.put(AvailableSettings.HBM2DDL_CREATE_SCHEMAS, true);
	}
	
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return false;
	}
	
	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return null;
	}
	
	public String getDefaultDatabaseName() {
		return defaultDatabaseName;
	}
}