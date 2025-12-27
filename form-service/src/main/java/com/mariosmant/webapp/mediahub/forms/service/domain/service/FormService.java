package com.mariosmant.webapp.mediahub.forms.service.domain.service;

import com.mariosmant.webapp.mediahub.forms.service.domain.dto.FormSubmitRequest;
import com.mariosmant.webapp.mediahub.forms.service.domain.model.Form;
import com.mariosmant.webapp.mediahub.forms.service.domain.port.AuditLogPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class FormService {

    private final AuditLogPort audit;
    private final CacheManager cacheManager;

    public FormService(AuditLogPort audit, CacheManager cacheManager) {
        this.audit = audit;
        this.cacheManager = Objects.requireNonNull(cacheManager);
    }

    @Transactional
    public String submitForm(String actor, FormSubmitRequest req) {
        // TODO Replace sample.
        Form form = new Form(null, req.getUsername(), req.getEmail(), req.getFirstName(), req.getLastName(), req.isEnabled(), Instant.now());
        String id = "test";
        audit.recordEvent(actor, "FORM_SUBMITTED", id, Map.of("username", form.getUsername()), Instant.now());

        evictFormCacheAfterCommit(id);

        return id;
    }

    private void evictFormCacheAfterCommit(String id) {
        Objects.requireNonNull(id, "id must not be null");

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "evictUserCacheAfterCommit must be called within an active transaction"
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        Cache cache = cacheManager.getCache("forms");
                        if (cache != null) {
                            cache.evict(id);
                        }
                    }
                }
        );
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "forms", key = "#id")
    public Optional<Form> getForm(String id) {
        // TODO Replace sample.
        return Optional.of(new Form());
    }


    @Transactional
    public void deleteForm(String actor, String id) {
        // TODO Replace sample.
        audit.recordEvent(actor, "FORM_DELETED", id, Map.of(), Instant.now());
        evictFormCacheAfterCommit(id);
    }

}
