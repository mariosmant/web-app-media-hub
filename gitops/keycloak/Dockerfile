FROM quay.io/keycloak/keycloak:26.5

# Build-time args
ARG KC_HTTP_RELATIVE_PATH
ARG KC_HTTPS_ENABLED
ARG KC_HTTPS_CERTIFICATE_FILE
ARG KC_HTTPS_CERTIFICATE_KEY_FILE
ARG KC_HTTP_ENABLED
ARG KC_HOSTNAME_STRICT
#ARG KC_HOSTNAME_STRICT_HTTPS
ARG KC_DB

# Convert to ENV so Keycloak sees them during build
ENV KC_HTTP_RELATIVE_PATH=${KC_HTTP_RELATIVE_PATH}
ENV KC_HTTPS_ENABLED=${KC_HTTPS_ENABLED}
ENV KC_HTTPS_CERTIFICATE_FILE=${KC_HTTPS_CERTIFICATE_FILE}
ENV KC_HTTPS_CERTIFICATE_KEY_FILE=${KC_HTTPS_CERTIFICATE_KEY_FILE}
ENV KC_HTTP_ENABLED=${KC_HTTP_ENABLED}
ENV KC_HOSTNAME_STRICT=${KC_HOSTNAME_STRICT}
#ENV KC_HOSTNAME_STRICT_HTTPS=${KC_HOSTNAME_STRICT_HTTPS}
ENV KC_DB=${KC_DB}

# Copy configuration
COPY conf/keycloak.conf /opt/keycloak/conf/keycloak.conf

# Copy custom providers (JARs)
COPY providers /opt/keycloak/providers

# Copy custom themes
COPY themes /opt/keycloak/themes

# Build the server (required before using --optimized)
RUN /opt/keycloak/bin/kc.sh build

# Default command (optimized mode)
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]
