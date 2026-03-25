#!/usr/bin/env sh

set -eu

. ./.env

docker compose run --rm acme acme.sh --renew \
    -d "$APP_DOMAIN" \
    --server letsencrypt_test \
    --dns dns_mousetrap \
    --debug 2 --force

./INSTALL_TOMCAT_CERTS.sh

# Tomcat reads certs at startup — full restart required after renewal
docker compose restart tomcat
