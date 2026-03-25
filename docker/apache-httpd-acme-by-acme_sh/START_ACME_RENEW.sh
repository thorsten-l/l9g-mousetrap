#!/usr/bin/env sh

set -eu

. ./.env

docker compose run --rm acme acme.sh --renew \
    -d "$APP_DOMAIN" \
    --server letsencrypt_test \
    --dns dns_mousetrap \
    --debug 2 --force

./INSTALL_APACHE_CERTS.sh

docker compose exec apache httpd -k graceful
