package com.mariosmant.webapp.mediahub.common.security.db.infrastructure.spring.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;

@Entity
@Table(
        name = "jwt_replays",
        indexes = {
                @Index(name = "idx_jwt_replays_jti", columnList = "jti", unique = true),
                @Index(name = "idx_jwt_replays_created_at", columnList = "created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA
public class JwtReplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // surrogate PK

    @Column(name = "jti", nullable = false, unique = true, length = 255)
    private String jti;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Explicit constructor for business key
    public JwtReplay(String jti) {
        this.jti = jti;
    }
}