package com.mariosmant.webapp.mediahub.forms.service.infrastructure.spring.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {}
