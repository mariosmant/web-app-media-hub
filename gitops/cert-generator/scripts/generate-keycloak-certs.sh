#!/bin/sh
set -e

# Requirements: openssl, keytool (from openjdk)
apk update >/dev/null
apk add --no-cache openssl >/dev/null
apk add --no-cache openjdk21-jre-headless >/dev/null

CERTS_DIR=/certs

CA_DIR="$CERTS_DIR/ca"

KEYCLOAK_DIR="$CERTS_DIR/keycloak"

mkdir -p "$CA_DIR" "$KEYCLOAK_DIR"


KEYCLOAK_KEYSTORE_PASSWORD="${KEYCLOAK_KEYSTORE_PASSWORD:-}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-}"
COUNTRY="${COUNTRY:-}"
STATE="${STATE:-}"
LOCALITY="${LOCALITY:-}"
ORG_NAME="${ORG_NAME:-}"
CA_CN="${CA_CN:-}"

echo "=== Generating Root CA (RSA 4096, SHA-384) ==="

if [ ! -f "$CA_DIR/root-keycloak-ca.key" ]; then
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$CA_DIR/root-keycloak-ca.key"
fi

if [ ! -f "$CA_DIR/root-keycloak-ca.crt" ]; then
  openssl req -x509 -new -key "$CA_DIR/root-keycloak-ca.key" -sha384 -days 3650 \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=Security/CN=$CA_CN" \
    -out "$CA_DIR/root-keycloak-ca.crt"
fi

ROOT_CA_CRT="$CA_DIR/root-keycloak-ca.crt"
ROOT_CA_KEY="$CA_DIR/root-keycloak-ca.key"

generate_node_cert_pem_multiple_san() {
  NODE_NAME="$1"
  NODE_DIR="$2"
  SAN_LIST="$3"   # space-separated SANs: "localhost keycloak 127.0.0.1"

  echo "=== Generating PEM cert for $NODE_NAME ==="
  mkdir -p "$NODE_DIR"

  # Private key
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$NODE_DIR/$NODE_NAME.key"

  # CSR
  openssl req -new -key "$NODE_DIR/$NODE_NAME.key" \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=Keycloak/CN=$NODE_NAME" \
    -out "$NODE_DIR/$NODE_NAME.csr"

  # SAN config file
  EXT_FILE="$NODE_DIR/$NODE_NAME.ext"
  echo "basicConstraints = CA:FALSE" > "$EXT_FILE"
  echo "keyUsage = digitalSignature, keyEncipherment" >> "$EXT_FILE"
  echo "extendedKeyUsage = serverAuth" >> "$EXT_FILE"
  echo "subjectAltName = @alt_names" >> "$EXT_FILE"
  echo "" >> "$EXT_FILE"
  echo "[alt_names]" >> "$EXT_FILE"

  i=1
  for san in $SAN_LIST; do
    # Detect IP vs DNS
    if echo "$san" | grep -Eq '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$'; then
      echo "IP.$i = $san" >> "$EXT_FILE"
    else
      echo "DNS.$i = $san" >> "$EXT_FILE"
    fi
    i=$((i+1))
  done

  # Sign CSR → PEM certificate
  openssl x509 -req \
    -in "$NODE_DIR/$NODE_NAME.csr" \
    -CA "$ROOT_CA_CRT" \
    -CAkey "$ROOT_CA_KEY" \
    -CAcreateserial \
    -out "$NODE_DIR/$NODE_NAME.crt" \
    -days 825 \
    -sha384 \
    -extfile "$EXT_FILE"

  echo "Generated PEM certificate:"
  echo "  $NODE_DIR/$NODE_NAME.crt"
  echo "Generated PEM key:"
  echo "  $NODE_DIR/$NODE_NAME.key"
}


# Controller and broker keystores
generate_node_cert_pem_multiple_san "keycloak" "$KEYCLOAK_DIR" "keycloak localhost 127.0.0.1 host.docker.internal"


echo "=== Generating truststore ==="

TRUSTSTORE_FILE="$TRUST_DIR/keycloak-truststore.p12"

if [ -f "$TRUSTSTORE_FILE" ]; then
  rm -f "$TRUSTSTORE_FILE"
fi

keytool -importcert \
  -alias myorg-root-ca \
  -file "$ROOT_CA_CRT" \
  -keystore "$TRUSTSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$TRUSTSTORE_PASSWORD" \
  -noprompt

echo "Truststore: $TRUSTSTORE_FILE"

echo "=== Setting secure permissions ==="

# Private keys: world-readable (keycloak runs as non-root)
find "$CERTS_DIR" -type f -name "*.key" -exec chmod 644 {} \;

# Keystores & truststores: world-readable (keycloak runs as non-root)
find "$CERTS_DIR" -type f -name "*-keystore.p12" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "kafka-truststore.p12" -exec chmod 644 {} \;

# Certificates & CSRs: world-readable
find "$CERTS_DIR" -type f -name "*.crt" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.csr" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.pem" -exec chmod 644 {} \;

echo "=== Permissions applied ==="

echo "=== Done ==="
