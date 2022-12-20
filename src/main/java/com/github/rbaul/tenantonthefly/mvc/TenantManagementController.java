package com.github.rbaul.tenantonthefly.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/tenants")
public class TenantManagementController {
	
	private final TenantManagementService tenantManagementService;
	
	@GetMapping("{tenantId}")
	public TenantDto getTenant(@PathVariable String tenantId) {
		return tenantManagementService.getById(tenantId);
	}
	
	@PostMapping
	public TenantDto create(
			@Validated @RequestBody TenantDto tenant) {
		return tenantManagementService.create(tenant);
	}
	
	@DeleteMapping("{tenantId}")
	public void delete(@PathVariable String tenantId) {
		tenantManagementService.delete(tenantId);
	}
	
	@GetMapping("search")
	public Page<TenantDto> fetch(@RequestParam(required = false) String filter, @PageableDefault Pageable pageable) {
		return tenantManagementService.search(filter, pageable);
	}
}
