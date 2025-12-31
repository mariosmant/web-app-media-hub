package com.mariosmant.webapp.mediahub.upload.service.domain.dto;


import com.mariosmant.webapp.mediahub.upload.service.domain.model.Form;

import java.time.Instant;

public class FormDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Instant createdAt;

    public static FormDto from(Form u) {
        FormDto dto = new FormDto();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.firstName = u.getFirstName();
        dto.lastName = u.getLastName();
        dto.enabled = u.isEnabled();
        dto.createdAt = u.getCreatedAt();
        return dto;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }
}
