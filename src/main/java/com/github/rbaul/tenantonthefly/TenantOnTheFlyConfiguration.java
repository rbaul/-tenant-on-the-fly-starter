package com.github.rbaul.tenantonthefly;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@AutoConfigureBefore({
		HibernateJpaAutoConfiguration.class,
		MongoAutoConfiguration.class
})
@ConditionalOnWebApplication
@AutoConfigurationPackage
@Configuration
@ComponentScan
@EnableConfigurationProperties
public class TenantOnTheFlyConfiguration {

}
