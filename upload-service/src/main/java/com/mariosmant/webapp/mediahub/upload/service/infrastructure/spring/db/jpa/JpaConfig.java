package com.mariosmant.webapp.mediahub.upload.service.infrastructure.spring.db.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = AuditEventRepository.class)
public class JpaConfig {}
