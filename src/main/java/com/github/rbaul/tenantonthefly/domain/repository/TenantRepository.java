package com.github.rbaul.tenantonthefly.domain.repository;

import com.github.rbaul.tenantonthefly.domain.model.Tenant;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TenantRepository extends PagingAndSortingRepository<Tenant, String> {
	List<Tenant> findAll();
}
