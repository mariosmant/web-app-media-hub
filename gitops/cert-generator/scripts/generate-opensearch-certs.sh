#!/bin/sh
set -e

# Requirements: openssl, keytool (optional)
apk update >/dev/null
apk add --no-cache openssl >/dev/null
apk add --no-cache openjdk21-jre-headless >/dev/null

CERTS_DIR=/certs

CA_DIR="$CERTS_DIR/ca"
OS_DIR="$CERTS_DIR/opensearch"
OSD_DIR="$CERTS_DIR/opensearch-dashboards"
TRUST_DIR="$CERTS_DIR/truststore"

mkdir -p "$CA_DIR" "$OS_DIR" "$OSD_DIR" "$TRUST_DIR"

COUNTRY="${COUNTRY:-}"
STATE="${STATE:-}"
LOCALITY="${LOCALITY:-}"
ORG_NAME="${ORG_NAME:-}"
CA_CN="${CA_CN:-OpenSearch-Root-CA}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-}"

echo "=== Generating Root CA (RSA 4096, SHA-384) ==="

if [ ! -f "$CA_DIR/root-os-ca.key" ]; then
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$CA_DIR/root-os-ca.key"
fi

if [ ! -f "$CA_DIR/root-os-ca.crt" ]; then
  openssl req -x509 -new -key "$CA_DIR/root-os-ca.key" -sha384 -days 3650 \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=Security/CN=$CA_CN" \
    -out "$CA_DIR/root-os-ca.crt"
fi

ROOT_CA_CRT="$CA_DIR/root-os-ca.crt"
ROOT_CA_KEY="$CA_DIR/root-os-ca.key"

generate_opensearch_cert() {
  CERT_NAME="$1"
  CERT_DIR="$2"

  echo "=== Generating cert for $CERT_NAME ==="
  mkdir -p "$CERT_DIR"

  # Private key
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$CERT_DIR/$CERT_NAME.key"

  # CSR
  openssl req -new -key "$CERT_DIR/$CERT_NAME.key" \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=OpenSearch/CN=$CERT_NAME" \
    -out "$CERT_DIR/$CERT_NAME.csr"

  # SAN config
  EXT_FILE="$CERT_DIR/$CERT_NAME.ext"
  cat > "$EXT_FILE" <<EOF
basicConstraints = CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = $CERT_NAME
DNS.2 = $CERT_NAME.appnet
DNS.3 = localhost
EOF

  # Sign CSR
  openssl x509 -req \
    -in "$CERT_DIR/$CERT_NAME.csr" \
    -CA "$ROOT_CA_CRT" \
    -CAkey "$ROOT_CA_KEY" \
    -CAcreateserial \
    -out "$CERT_DIR/$CERT_NAME.crt" \
    -days 825 \
    -sha384 \
    -extfile "$EXT_FILE"

  echo "Generated certificate: $CERT_DIR/$CERT_NAME.crt"
}

# === OpenSearch HTTP certificate ===
generate_opensearch_cert "opensearch" "$OS_DIR"

# === OpenSearch Transport certificate ===
generate_opensearch_cert "opensearch-transport" "$OS_DIR"

# === Dashboards certificate (optional but recommended) ===
generate_opensearch_cert "opensearch-dashboards" "$OSD_DIR"

echo "=== Generating truststore for Dashboards ==="

TRUSTSTORE_FILE="$TRUST_DIR/opensearch-truststore.p12"

if [ -f "$TRUSTSTORE_FILE" ]; then
  rm -f "$TRUSTSTORE_FILE"
fi

keytool -importcert \
  -alias opensearch-root-ca \
  -file "$ROOT_CA_CRT" \
  -keystore "$TRUSTSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$TRUSTSTORE_PASSWORD" \
  -noprompt

echo "Truststore: $TRUSTSTORE_FILE"

echo "=== Setting secure permissions ==="

# Private keys: owner read/write only
find "$CERTS_DIR" -type f -name "*.key" -exec chmod 644 {} \;

# Certificates & CSRs: world-readable
find "$CERTS_DIR" -type f -name "*.crt" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.csr" -exec chmod 644 {} \;

# Truststore: world-readable (Dashboards runs as non-root)
chmod 644 "$TRUSTSTORE_FILE"

echo "=== Done ==="
