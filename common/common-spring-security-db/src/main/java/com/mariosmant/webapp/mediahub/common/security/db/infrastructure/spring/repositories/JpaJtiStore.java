package com.mariosmant.webapp.mediahub.common.security.db.infrastructure.spring.repositories;

import com.mariosmant.webapp.mediahub.common.security.context.domain.service.JtiStore;
import com.mariosmant.webapp.mediahub.common.security.db.infrastructure.spring.entities.JwtReplay;

public class JpaJtiStore implements JtiStore {

    private final JwtReplayRepository repository;

    public JpaJtiStore(JwtReplayRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean seen(String jti) {
        return repository.existsByJti(jti);
    }

    @Override
    public void remember(String jti) {
        repository.save(new JwtReplay(jti));
    }
}