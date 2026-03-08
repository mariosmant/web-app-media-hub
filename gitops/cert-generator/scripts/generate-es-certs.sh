#!/bin/sh
set -e

apk update >/dev/null
apk add --no-cache openssl >/dev/null
apk add --no-cache openjdk21-jre-headless >/dev/null

CERTS_DIR=/certs

CA_DIR="$CERTS_DIR/ca"
ES01_DIR="$CERTS_DIR/es01"
ES02_DIR="$CERTS_DIR/es02"
ES03_DIR="$CERTS_DIR/es03"
TRUST_DIR="$CERTS_DIR/truststore"

mkdir -p "$CA_DIR" "$ES01_DIR" "$ES02_DIR" "$ES03_DIR" "$TRUST_DIR"

ES01_KEYSTORE_PASSWORD="${ES01_KEYSTORE_PASSWORD:-}"
ES02_KEYSTORE_PASSWORD="${ES01_KEYSTORE_PASSWORD:-}"
ES03_KEYSTORE_PASSWORD="${ES01_KEYSTORE_PASSWORD:-}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-}"

COUNTRY="${COUNTRY:-}"
STATE="${STATE:-}"
LOCALITY="${LOCALITY:-}"
ORG_NAME="${ORG_NAME:-}"
CA_CN="${CA_CN:-}"

echo "=== Generating Root CA for Elasticsearch ==="

if [ ! -f "$CA_DIR/root-es-ca.key" ]; then
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$CA_DIR/root-es-ca.key"
fi

if [ ! -f "$CA_DIR/root-es-ca.crt" ]; then
  openssl req -x509 -new -key "$CA_DIR/root-es-ca.key" -sha384 -days 3650 \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=Elasticsearch/CN=$CA_CN" \
    -out "$CA_DIR/root-es-ca.crt"
fi

ROOT_CA_CRT="$CA_DIR/root-es-ca.crt"
ROOT_CA_KEY="$CA_DIR/root-es-ca.key"

generate_es_node() {
  NODE_NAME="$1"
  NODE_DIR="$2"
  KEYSTORE_PASSWORD="$3"

  echo "=== Generating cert for $NODE_NAME ==="
  mkdir -p "$NODE_DIR"

  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$NODE_DIR/$NODE_NAME.key"

  openssl req -new -key "$NODE_DIR/$NODE_NAME.key" \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=Elasticsearch/CN=$NODE_NAME" \
    -out "$NODE_DIR/$NODE_NAME.csr"

  EXT_FILE="$NODE_DIR/$NODE_NAME.ext"
  cat > "$EXT_FILE" <<EOF
basicConstraints = CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = $NODE_NAME
DNS.2 = $NODE_NAME.appnet
DNS.3 = localhost
EOF

  openssl x509 -req \
    -in "$NODE_DIR/$NODE_NAME.csr" \
    -CA "$ROOT_CA_CRT" \
    -CAkey "$ROOT_CA_KEY" \
    -CAcreateserial \
    -out "$NODE_DIR/$NODE_NAME.crt" \
    -days 825 \
    -sha384 \
    -extfile "$EXT_FILE"

  openssl pkcs12 -export \
    -in "$NODE_DIR/$NODE_NAME.crt" \
    -inkey "$NODE_DIR/$NODE_NAME.key" \
    -certfile "$ROOT_CA_CRT" \
    -name "$NODE_NAME" \
    -out "$NODE_DIR/$NODE_NAME.p12" \
    -passout pass:"$KEYSTORE_PASSWORD"

  echo "Generated keystore: $NODE_DIR/$NODE_NAME.p12"
}

generate_es_node "es01" "$ES01_DIR" "$ES01_KEYSTORE_PASSWORD"
generate_es_node "es02" "$ES02_DIR" "$ES02_KEYSTORE_PASSWORD"
generate_es_node "es03" "$ES03_DIR" "$ES03_KEYSTORE_PASSWORD"

echo "=== Generating Elasticsearch truststore ==="

TRUSTSTORE_FILE="$TRUST_DIR/es-truststore.p12"

if [ -f "$TRUSTSTORE_FILE" ]; then
  rm -f "$TRUSTSTORE_FILE"
fi

keytool -importcert \
  -alias es-root-ca \
  -file "$ROOT_CA_CRT" \
  -keystore "$TRUSTSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$TRUSTSTORE_PASSWORD" \
  -noprompt

echo "Truststore: $TRUSTSTORE_FILE"

echo "=== Setting secure permissions ==="

find "$CERTS_DIR" -type f -name "*.key" -exec chmod 600 {} \;
find "$CERTS_DIR" -type f -name "*.p12" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.crt" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.csr" -exec chmod 644 {} \;

echo "=== Elasticsearch certificate generation complete ==="
