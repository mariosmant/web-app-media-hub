# mTLS prerequisites

## ECDSA P‑384 client cert
### Create an OpenSSL config with SAN and clientAuth EKU
```bash
cat > san-client-ecdsa.cnf <<'EOF'
[ req ]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no
default_md = sha384

[ req_distinguished_name ]
CN = config-service

[ v3_req ]
subjectAltName = @alt_names
extendedKeyUsage = clientAuth
keyUsage = digitalSignature

[ alt_names ]
DNS.1 = localhost
IP.1 = 127.0.0.1
IP.2 = ::1
EOF
```
### Generate ECDSA P-384 private key
```bash
openssl ecparam -name secp384r1 -genkey -noout -out client-ecdsa.key
```

### Create self-signed client certificate (for lab)
```bash
openssl req -new -x509 -days 3650 -key client-ecdsa.key -out client-ecdsa.crt \
-config san-client-ecdsa.cnf
```

## Hardened PKCS#12 keystore (AES-256) for the client certificate. For ECDSA:
```bash
openssl pkcs12 -export -inkey client-ecdsa.key -in client-ecdsa.crt \
  -out client-keystore.p12 -name config-service -macalg sha256 -keypbe AES-256-CBC -certpbe AES-256-CBC

```

## Truststore for Keycloak’s server certificate or your CA
### If Keycloak uses a self-signed cert for localhost, export its server cert:
[keycloak.crt should be the server certificate (or your CA certificate)]
```bash
openssl pkcs12 -export -in keycloak.crt -nokeys \
-out client-truststore.p12 -name keycloak -password pass:changeit
```
