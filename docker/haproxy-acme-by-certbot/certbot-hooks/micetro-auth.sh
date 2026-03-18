#!/usr/bin/env sh

set -eu

if [ -z "${MICETRO_ZONE:-}" ] || [ -z "${MICETRO_TOKEN:-}" ] || [ -z "${MICETRO_API_URL:-}" ]; then
    echo "Error: MICETRO_ZONE, MICETRO_TOKEN or MICETRO_API_URL environment variable is missing." >&2
    exit 1
fi

if [ -z "${CERTBOT_DOMAIN:-}" ] || [ -z "${CERTBOT_VALIDATION:-}" ]; then
    echo "Error: CERTBOT_DOMAIN or CERTBOT_VALIDATION is missing." >&2
    exit 1
fi

FQDN="_acme-challenge.${CERTBOT_DOMAIN}"
NAME="${FQDN%.$MICETRO_ZONE}"

echo "Certbot auth hook: adding TXT for ${FQDN}" >&2

curl -fsS -X POST "$MICETRO_API_URL" \
    -H "Authorization: Bearer $MICETRO_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"zone\": \"$MICETRO_ZONE\",
      \"name\": \"$NAME\",
      \"data\": \"$CERTBOT_VALIDATION\"
    }"

# Warten auf DNS-Propagation
sleep 30
