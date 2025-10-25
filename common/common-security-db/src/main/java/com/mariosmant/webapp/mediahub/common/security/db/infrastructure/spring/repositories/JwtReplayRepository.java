package com.mariosmant.webapp.mediahub.common.security.db.infrastructure.spring.repositories;

import com.mariosmant.webapp.mediahub.common.security.db.infrastructure.spring.entities.JwtReplay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtReplayRepository extends JpaRepository<JwtReplay, Long> {
    boolean existsByJti(String jti);
}