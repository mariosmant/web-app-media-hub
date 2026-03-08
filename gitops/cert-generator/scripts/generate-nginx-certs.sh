#!/bin/sh
set -e

apk update >/dev/null
apk add --no-cache openssl >/dev/null
apk add --no-cache openjdk21-jre-headless >/dev/null

CERTS_DIR=/certs

CA_DIR="$CERTS_DIR/ca"
NGINX_DIR="$CERTS_DIR/nginx"
TRUST_DIR="$CERTS_DIR/truststore"

mkdir -p "$CA_DIR" "$NGINX_DIR" "$TRUST_DIR"

NGINX_KEYSTORE_PASSWORD="${NGINX_KEYSTORE_PASSWORD:-}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-}"

COUNTRY="${COUNTRY:-}"
STATE="${STATE:-}"
LOCALITY="${LOCALITY:-}"
ORG_NAME="${ORG_NAME:-}"
CA_CN="${CA_CN:-}"

echo "=== Generating Root CA for NGINX ==="

if [ ! -f "$CA_DIR/root-nginx-ca.key" ]; then
  openssl genpkey -algorithm RSA \
    -pkeyopt rsa_keygen_bits:4096 \
    -out "$CA_DIR/root-nginx-ca.key"
fi

if [ ! -f "$CA_DIR/root-nginx-ca.crt" ]; then
  openssl req -x509 -new -key "$CA_DIR/root-nginx-ca.key" -sha384 -days 3650 \
    -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=NGINX/CN=$CA_CN" \
    -out "$CA_DIR/root-nginx-ca.crt"
fi

ROOT_CA_CRT="$CA_DIR/root-nginx-ca.crt"
ROOT_CA_KEY="$CA_DIR/root-nginx-ca.key"

echo "=== Generating NGINX server certificate ==="

openssl genpkey -algorithm RSA \
  -pkeyopt rsa_keygen_bits:4096 \
  -out "$NGINX_DIR/nginx.key"

openssl req -new -key "$NGINX_DIR/nginx.key" \
  -subj "/C=$COUNTRY/ST=$STATE/L=$LOCALITY/O=$ORG_NAME/OU=NGINX/CN=nginx" \
  -out "$NGINX_DIR/nginx.csr"

EXT_FILE="$NGINX_DIR/nginx.ext"
cat > "$EXT_FILE" <<EOF
basicConstraints = CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = nginx
DNS.2 = nginx.appnet
DNS.3 = localhost
EOF

openssl x509 -req \
  -in "$NGINX_DIR/nginx.csr" \
  -CA "$ROOT_CA_CRT" \
  -CAkey "$ROOT_CA_KEY" \
  -CAcreateserial \
  -out "$NGINX_DIR/nginx.crt" \
  -days 825 \
  -sha384 \
  -extfile "$EXT_FILE"

openssl pkcs12 -export \
  -in "$NGINX_DIR/nginx.crt" \
  -inkey "$NGINX_DIR/nginx.key" \
  -certfile "$ROOT_CA_CRT" \
  -name "nginx" \
  -out "$NGINX_DIR/nginx.p12" \
  -passout pass:"$NGINX_KEYSTORE_PASSWORD"

echo "=== Generating NGINX truststore ==="

TRUSTSTORE_FILE="$TRUST_DIR/nginx-truststore.p12"

if [ -f "$TRUSTSTORE_FILE" ]; then
  rm -f "$TRUSTSTORE_FILE"
fi

keytool -importcert \
  -alias nginx-root-ca \
  -file "$ROOT_CA_CRT" \
  -keystore "$TRUSTSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$TRUSTSTORE_PASSWORD" \
  -noprompt

echo "=== Setting secure permissions ==="

find "$CERTS_DIR" -type f -name "*.key" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.p12" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.crt" -exec chmod 644 {} \;
find "$CERTS_DIR" -type f -name "*.csr" -exec chmod 644 {} \;

echo "=== NGINX certificate generation complete ==="
