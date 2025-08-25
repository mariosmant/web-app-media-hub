package com.mariosmant.webapp.mediahub.user.service.domain.dto;

import com.mariosmant.webapp.mediahub.common.utils.annotations.NoHtml;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {
    @NotBlank @Size(max = 64) @NoHtml
    private String username;

    @NotBlank @Email @Size(max = 190) @NoHtml
    private String email;

    @NotBlank @Size(max = 80) @NoHtml
    private String firstName;

    @NotBlank @Size(max = 80) @NoHtml
    private String lastName;

    private boolean enabled = true;

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
}
