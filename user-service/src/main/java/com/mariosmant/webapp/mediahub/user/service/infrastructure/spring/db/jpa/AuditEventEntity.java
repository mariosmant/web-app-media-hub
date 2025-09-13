package com.mariosmant.webapp.mediahub.user.service.infrastructure.spring.db.jpa;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(length = 190)
    private String actor;

    @Column(length = 64, nullable = false)
    private String eventAction;

    @Column(length = 64)
    private String subjectId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    public Long getId() { return id; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getEventAction() { return eventAction; }
    public void setEventAction(String eventAction) { this.eventAction = eventAction; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
}
