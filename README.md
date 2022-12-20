# Tenant on the fly
> Migrate any Spring Boot Application to multi tenancy with "zero code" only add this library.

> Support any SQL Database with separate database approach and NoSQL only MongoDB.

> Support MVC/Reactive/Async/RabbitMQ interceptors (propagations of tenant id)

> Support Server/Client for microservice architecture to create tenant in run time in all microservices.

## Properties
```properites
// SQL
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=false
 
tenant.on-the-fly.notifier-type=RABBIT_MQ
 
// Only for Controller microservice of tenant management
tenant.on-the-fly.controller=true
```

## Run with all tenants
```java
@Autowired
private MultiTenantExecuteService multiTenantExecuteService;
 
@Override
public void onApplicationEvent(ApplicationReadyEvent event) {
    multiTenantExecuteService.executeForAllTenants(() -> {
        // Bussiness
    });
}
```

## Add beans for additional execution create/drop tenant
```java
@Service
public class CustomTenantDatabaseService implements TenantDatabaseService {
	
	@Override
	public void init() {
		// Additional init execution
	}
	
	@Override
	public void drop() {
		// Additional drop execution
	}
}
```