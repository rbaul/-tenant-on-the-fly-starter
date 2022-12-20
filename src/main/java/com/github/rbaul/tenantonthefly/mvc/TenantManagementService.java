package com.github.rbaul.tenantonthefly.mvc;

import com.github.rbaul.tenantonthefly.TenantDatabaseService;
import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import com.github.rbaul.tenantonthefly.domain.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class TenantManagementService {
	
	public static final String DEFAULT_TENANT_NAME = "default";
	public static final String DEFAULT_TENANT_DATABASE_NAME = "default";
	public static final String DEFAULT_TENANT_DESCRIPTION = "Default tenant";
	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired(required = false)
	private List<TenantDatabaseService> tenantDatabaseServices;
	
	@Autowired
	private MultiTenantExecuteService multiTenantExecuteService;
	
	@Autowired(required = false)
	private List<TenantUpdatePublisher> tenantUpdatePublishers;
	
	public void createDefaultTenant() {
		TenantDto tenant = TenantDto.builder()
				.name(DEFAULT_TENANT_NAME)
				.description(DEFAULT_TENANT_DESCRIPTION).build();
		
		this.create(tenant);
	}
	
	public final TenantDto create(TenantDto tenantDto) {
		boolean alreadyExist = tenantRepository.existsById(tenantDto.getName());
		
		if (alreadyExist) {
			return null;
		}
		
		Tenant entity = TenantOnTheFlyConverter.tenantDtoToTenant(tenantDto);
		entity.setDatabaseName(generateDatabaseName(tenantDto.getName()));
		Tenant tenant = tenantRepository.save(entity);
		
		multiTenantExecuteService.execute(tenant,
				() -> initTenantDatabase(tenant.getDatabaseName()));
		
		
		TenantDto tenantResponse = TenantOnTheFlyConverter.tenantToTenantDto(tenant);
		
		if (tenantUpdatePublishers != null) {
			tenantUpdatePublishers.forEach(tenantUpdatePublisher -> tenantUpdatePublisher.created(tenantResponse));
		}
		return tenantResponse;
	}
	
	public final void delete(String tenantName) {
		Tenant tenant = tenantRepository.findById(tenantName)
				.orElseThrow(() -> new EmptyResultDataAccessException("No found tenant with id: " + tenantName, 1));
		;
		
		multiTenantExecuteService.execute(tenant, () -> {
			dropTenantDatabase(tenant.getDatabaseName());
		});
		tenantRepository.deleteById(tenantName);
		
		if (tenantUpdatePublishers != null) {
			tenantUpdatePublishers.forEach(tenantUpdatePublisher -> tenantUpdatePublisher.deleted(tenantName));
		}
	}
	
	public final Page<TenantDto> search(String filter, Pageable pageable) {
		return tenantRepository.findAll(pageable)
				.map(TenantOnTheFlyConverter::tenantToTenantDto);
	}
	
	public final TenantDto getById(String tenantId) {
		return tenantRepository.findById(tenantId)
				.map(TenantOnTheFlyConverter::tenantToTenantDto)
				.orElseThrow(() -> new EmptyResultDataAccessException("No found tenant with id: " + tenantId, 1));
	}
	
	protected void initTenantDatabase(String databaseName) {
		if (tenantDatabaseServices != null) {
			tenantDatabaseServices.forEach(TenantDatabaseService::init);
		}
	}
	
	protected void dropTenantDatabase(String databaseName) {
		if (tenantDatabaseServices != null) {
			tenantDatabaseServices.forEach(TenantDatabaseService::drop);
		}
	}
	
	protected String generateDatabaseName(String tenantName) {
		return String.format("%s_%s", getDefaultDatabaseName(), getTenantValidDatabaseName(tenantName));
	}
	
	private String getTenantValidDatabaseName(String tenantName) {
		return tenantName.replaceAll("[^A-Za-z0-9_*]+", "_").toLowerCase();
	}
	
	protected String getDefaultDatabaseName() {
		return "";
	}
	
}
