package com.github.rbaul.tenantonthefly.hibernate;

import com.github.rbaul.tenantonthefly.TenantOnTheFlyProperties;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, EntityManager.class, SessionImplementor.class})
@Configuration
public class MultiTenantHibernateConfig {
	
	@Bean
	public TenantIdentifierResolver tenantIdentifierResolver() {
		return new TenantIdentifierResolver();
	}
	
	@Bean
	public SchemaConfigurableMultiTenantConnectionProvider schemaConfigurableMultiTenantConnectionProvider(
			DataSource dataSource,
			HibernateProperties hibernateProperties) {
		return new SchemaConfigurableMultiTenantConnectionProvider(dataSource, hibernateProperties);
	}
	
	@Bean
	public TenantManagementHibernateService tenantManagementHibernateService(DataSource dataSource,
																			 SchemaConfigurableMultiTenantConnectionProvider schemaConfigurableMultiTenantConnectionProvider,
																			 TenantRepository tenantRepository,
																			 TenantOnTheFlyProperties tenantOnTheFlyProperties) {
		return new TenantManagementHibernateService(dataSource, schemaConfigurableMultiTenantConnectionProvider, tenantRepository, tenantOnTheFlyProperties);
	}
	
}
