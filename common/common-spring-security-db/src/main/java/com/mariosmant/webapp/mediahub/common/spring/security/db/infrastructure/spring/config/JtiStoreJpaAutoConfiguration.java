package com.mariosmant.webapp.mediahub.common.spring.security.db.infrastructure.spring.config;


import com.mariosmant.webapp.mediahub.common.spring.security.context.domain.service.JtiStore;
import com.mariosmant.webapp.mediahub.common.spring.security.db.infrastructure.spring.repositories.JpaJtiStore;
import com.mariosmant.webapp.mediahub.common.spring.security.db.infrastructure.spring.repositories.JwtReplayRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(EntityManager.class) // Only load if JPA is present.
@ConditionalOnProperty(prefix = "security.validator-policy", name = "jti-store-method", havingValue = "jpa", matchIfMissing = true)
public class JtiStoreJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JtiStore jtiStore(JwtReplayRepository repository) {
        return new JpaJtiStore(repository);
    }
}
