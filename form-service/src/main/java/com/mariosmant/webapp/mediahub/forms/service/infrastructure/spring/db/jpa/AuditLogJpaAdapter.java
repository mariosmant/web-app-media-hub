package com.mariosmant.webapp.mediahub.forms.service.infrastructure.spring.db.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariosmant.webapp.mediahub.forms.service.domain.port.AuditLogPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Component
public class AuditLogJpaAdapter implements AuditLogPort {

    private final AuditEventRepository repo;
    private final ObjectMapper mapper;

    public AuditLogJpaAdapter(AuditEventRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void recordEvent(String actor, String action, String subjectId, Map<String, Object> details, Instant at) {
        AuditEventEntity e = new AuditEventEntity();
        e.setOccurredAt(at != null ? at : Instant.now());
        e.setActor(actor);
        e.setEventAction(action);
        e.setSubjectId(subjectId);
        try {
            e.setDetailsJson(details != null ? mapper.writeValueAsString(details) : "{}");
        } catch (JsonProcessingException ex) {
            e.setDetailsJson("{}");
        }
        repo.save(e);
    }
}
