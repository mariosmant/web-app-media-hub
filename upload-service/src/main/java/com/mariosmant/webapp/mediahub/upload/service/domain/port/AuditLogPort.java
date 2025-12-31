package com.mariosmant.webapp.mediahub.upload.service.domain.port;

import java.time.Instant;
import java.util.Map;

public interface AuditLogPort {
    void recordEvent(String actor, String action, String subjectId, Map<String, Object> details, Instant at);
}
