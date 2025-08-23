package com.mariosmant.webapp.mediahub.user.service.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Getter
//@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.redis")
public class AppRedisProperties {

    private String host = "localhost";
    private int port = 6379;

    /**
     * Stored as char[] for secure wiping.
     * Excluded from Lombok-generated toString via @ToString.Exclude,
     * excluded from JSON via @JsonIgnore,
     * and overridden in toString() for masking.
     */
    @lombok.ToString.Exclude
    @JsonIgnore
    private char[] password;

    private boolean ssl = false;
    private boolean sentinel = false;
    private String sentinelMaster;
    private List<String> sentinelNodes;

    // === Secure password handling (manual) ===

    public char[] getPassword() {
        return password != null ? Arrays.copyOf(password, password.length) : null;
    }

    public void setPassword(String password) {
        clearPassword();
        if (password != null) {
            this.password = password.toCharArray();
        }
    }

    public void setPassword(char[] password) {
        clearPassword();
        if (password != null) {
            this.password = Arrays.copyOf(password, password.length);
        }
    }

    public boolean hasPassword() {
        return password != null && password.length > 0;
    }

    public void clearPassword() {
        if (this.password != null) {
            Arrays.fill(this.password, '\0'); // securely wipe
            this.password = null;
        }
    }

    @Override
    public String toString() {
        return "AppRedisProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", password=" + (hasPassword() ? "[PROTECTED]" : "null") +
                ", ssl=" + ssl +
                ", sentinel=" + sentinel +
                ", sentinelMaster='" + sentinelMaster + '\'' +
                ", sentinelNodes=" + sentinelNodes +
                '}';
    }
}
