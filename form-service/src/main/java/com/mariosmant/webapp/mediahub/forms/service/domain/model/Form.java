package com.mariosmant.webapp.mediahub.forms.service.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Form {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Instant createdAt;

    public Form() {}

    public Form(String id, String username, String email, String firstName, String lastName, boolean enabled, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Form user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
