#!/usr/bin/env sh

set -eu

. ./.env

docker compose run --rm certbot certonly \
    --manual \
    --preferred-challenges dns \
    --manual-auth-hook /hooks/mousetrap-auth.sh \
    --manual-cleanup-hook /hooks/mousetrap-cleanup.sh \
    --non-interactive \
    --agree-tos \
    --email "$ACME_EMAIL" \
    --server https://acme-staging-v02.api.letsencrypt.org/directory \
    -d "$APP_DOMAIN"

./BUILD_HAPROXY_PEM_CERTBOT.sh
