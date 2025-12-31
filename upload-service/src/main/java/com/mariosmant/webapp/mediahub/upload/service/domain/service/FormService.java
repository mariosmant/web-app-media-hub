package com.mariosmant.webapp.mediahub.upload.service.domain.service;

import com.mariosmant.webapp.mediahub.upload.service.domain.dto.FormSubmitRequest;
import com.mariosmant.webapp.mediahub.upload.service.domain.model.Form;
import com.mariosmant.webapp.mediahub.upload.service.domain.port.AuditLogPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
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
    public String submitForm(FormSubmitRequest req) {
        return submitFormInternal(req, null, "FORM_SUBMITTED");
    }

    @Transactional
    public String submitFormMultipart(FormSubmitRequest req, Map<String, List<MultipartFile>> files) {
        return submitFormInternal(req, files, "FORM_SUBMITTED_MULTIPART");
    }

    private String submitFormInternal(FormSubmitRequest req, Map<String, List<MultipartFile>> files, String action) {
        String actor = "testactor";

        Instant now = Instant.now();

        // 1. Application-level validation
//        validateFiles(files);
//        validateUserExists(req.getUsername());
//        validateActorPermissions(actor, action);
//        validateBusinessPreconditions(req);

        // 2. Domain creation (domain invariants inside)
        Form form = new Form();//Form.create(req, now); // domain-level validation inside constructor

        // 3. Persist
        String id = "test";//formRepository.save(form);

        // 4. Side effects
        audit.recordEvent(actor, action, id, Map.of("username", form.getUsername()), now);
        evictFormCacheAfterCommit(id);
        return id;
    }

    private void evictFormCacheAfterCommit(String id) throws IllegalStateException {
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
