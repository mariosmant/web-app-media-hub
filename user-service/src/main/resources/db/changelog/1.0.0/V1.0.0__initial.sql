CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL,
    actor VARCHAR(190),
    event_action VARCHAR(64) NOT NULL,
    subject_id VARCHAR(64),
    details_json TEXT
);

-- Optional: indexes for performance
CREATE INDEX idx_audit_events_occurred_at ON audit_events (occurred_at);
CREATE INDEX idx_audit_events_event_action ON audit_events (event_action);
