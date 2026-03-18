#!/usr/bin/env sh

set -eu

. ./.env

docker compose run --rm certbot renew \
    --manual \
    --preferred-challenges dns \
    --manual-auth-hook /hooks/micetro-auth.sh \
    --manual-cleanup-hook /hooks/micetro-cleanup.sh \
    --server https://acme-staging-v02.api.letsencrypt.org/directory

./BUILD_HAPROXY_PEM_CERTBOT.sh

docker compose restart haproxy
