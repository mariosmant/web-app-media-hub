package com.mariosmant.webapp.mediahub.user.service.domain.service;

import com.mariosmant.webapp.mediahub.user.service.domain.dto.CreateUserRequest;
import com.mariosmant.webapp.mediahub.user.service.domain.dto.UpdateUserRequest;
import com.mariosmant.webapp.mediahub.user.service.domain.model.User;
import com.mariosmant.webapp.mediahub.user.service.domain.port.AuditLogPort;
import com.mariosmant.webapp.mediahub.user.service.domain.port.IdentityUserPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final IdentityUserPort idp;
    private final AuditLogPort audit;

    public UserService(IdentityUserPort idp, AuditLogPort audit) {
        this.idp = idp;
        this.audit = audit;
    }

    @Transactional
    public String createUser(String actor, CreateUserRequest req) {
        User u = new User(null, req.getUsername(), req.getEmail(), req.getFirstName(), req.getLastName(), req.isEnabled(), Instant.now());
        String id = idp.create(u);
        audit.recordEvent(actor, "USER_CREATED", id, Map.of("username", u.getUsername()), Instant.now());
        return id;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users", key = "#id")
    public Optional<User> getUser(String id) {
        return idp.getById(id);
    }

    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public void updateUser(String actor, String id, UpdateUserRequest req) {
        User current = idp.getById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (req.getEmail() != null) current.setEmail(req.getEmail());
        if (req.getFirstName() != null) current.setFirstName(req.getFirstName());
        if (req.getLastName() != null) current.setLastName(req.getLastName());
        if (req.getEnabled() != null) current.setEnabled(req.getEnabled());
        idp.update(id, current);
        audit.recordEvent(actor, "USER_UPDATED", id, Map.of("fields", "partial"), Instant.now());
    }

    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public void deleteUser(String actor, String id) {
        idp.delete(id);
        audit.recordEvent(actor, "USER_DELETED", id, Map.of(), Instant.now());
    }

    @Transactional(readOnly = true)
    public List<User> search(String q, int offset, int limit) {
        return idp.search(q, offset, limit);
    }
}
