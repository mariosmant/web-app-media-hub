package com.mariosmant.webapp.mediahub.user.service.domain.dto;

import com.mariosmant.webapp.mediahub.common.utils.web.annotations.NoHtml;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Email @Size(max = 190) @NoHtml
    private String email;

    @Size(max = 80) @NoHtml
    private String firstName;

    @Size(max = 80) @NoHtml
    private String lastName;

    private Boolean enabled;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
