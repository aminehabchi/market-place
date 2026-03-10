#!/bin/bash

mkdir -p certs

# 1. Create a private CA
openssl genrsa -out certs/ca.key 4096
openssl req -new -x509 -days 3650 -key certs/ca.key \
  -out certs/ca.crt \
  -subj "/CN=internal-ca/O=MyApp"

# 2. Generate cert for each service
for SERVICE in gateway users-service media-service products-service eureka-server; do
  openssl genrsa -out certs/$SERVICE.key 2048
  openssl req -new -key certs/$SERVICE.key \
    -out certs/$SERVICE.csr \
    -subj "/CN=$SERVICE/O=MyApp"
  openssl x509 -req -days 365 \
    -in certs/$SERVICE.csr \
    -CA certs/ca.crt -CAkey certs/ca.key -CAcreateserial \
    -out certs/$SERVICE.crt
  
  # Package as PKCS12 for Spring Boot
  openssl pkcs12 -export \
    -in certs/$SERVICE.crt \
    -inkey certs/$SERVICE.key \
    -out certs/$SERVICE.p12 \
    -name $SERVICE \
    -passout pass:123123
done
