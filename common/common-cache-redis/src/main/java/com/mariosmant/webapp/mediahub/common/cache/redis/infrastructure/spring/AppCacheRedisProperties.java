package com.mariosmant.webapp.mediahub.common.cache.redis.infrastructure.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.cache.redis")
public class AppCacheRedisProperties {
    private Boolean trasactionAware;
    private LettuceSpecProperties lettuce;
    private DefaultsSpecProperties defaults;
    private List<CacheSpecProperties> caches = new ArrayList<>();

    @Getter
    static class LettuceSpecProperties {
        private String host;
        private Integer port;
        /**
         * Stored as char[] for secure wiping.
         * Excluded from Lombok-generated toString via @ToString.Exclude,
         * excluded from JSON via @JsonIgnore,
         * and overridden in toString() for masking.
         */
        @ToString.Exclude
        @JsonIgnore
        private char[] password;
        @Setter
        private Boolean useSsl;
        @Setter
        private boolean sentinel;
        @Setter
        private String sentinelMaster;
        @Setter
        private List<String> sentinelNodes;
        @Setter
        private LettucePoolSpecProperties pool;
        @Setter
        private LettucePoolSpecProperties transactionsPool;
        @Setter
        private Duration commandTimeout;
        @Setter
        private Duration connectTimeout;
        @Setter
        private Duration shutdownTimeout;


        public char[] getPassword() {
            return password != null ? Arrays.copyOf(password, password.length) : null;
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
            return "AppCacheRedisProperties.LettuceSpecProperties{" +
                    "host='" + host + '\'' +
                    ", port=" + port +
                    ", password=" + (hasPassword() ? "[PROTECTED]" : "null") +
                    ", useSsl=" + useSsl +
                    ", sentinel=" + sentinel +
                    ", sentinelMaster='" + sentinelMaster + '\'' +
                    ", sentinelNodes=" + sentinelNodes +
                    ", pool=" + pool +
                    ", commandTimeout=" + commandTimeout +
                    ", connectTimeout=" + connectTimeout +
                    ", shutdownTimeout=" + shutdownTimeout +
                    '}';
        }
    }

    @Setter
    @Getter
    @ToString
    static class DefaultsSpecProperties {
        private Duration ttl;
        private RedisSerializerType valueSerializer;
    }

    @Setter
    @Getter
    @ToString
    static class LettucePoolSpecProperties {
        private Boolean enabled;
        private Integer maxTotal;
        private Integer maxIdle;
        private Integer minIdle;
        private Duration maxWait;
    }



    @Setter
    @Getter
    @ToString
    static class CacheSpecProperties {
        private String name;
        private Duration ttl;
        private RedisSerializerType valueSerializer;
    }

}
