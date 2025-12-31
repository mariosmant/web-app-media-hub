package com.mariosmant.webapp.mediahub.upload.service.infrastructure.spring.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {}
