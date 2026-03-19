#!/usr/bin/env sh

set -eu

if [ -z "${MOUSETRAP_ZONE:-}" ] || [ -z "${MOUSETRAP_TOKEN:-}" ] || [ -z "${MOUSETRAP_API_URL:-}" ]; then
    echo "Error: MOUSETRAP_ZONE, MOUSETRAP_TOKEN or MOUSETRAP_API_URL environment variable is missing." >&2
    exit 1
fi

if [ -z "${CERTBOT_DOMAIN:-}" ] || [ -z "${CERTBOT_VALIDATION:-}" ]; then
    echo "Error: CERTBOT_DOMAIN or CERTBOT_VALIDATION is missing." >&2
    exit 1
fi

FQDN="_acme-challenge.${CERTBOT_DOMAIN}"
NAME="${FQDN%.$MOUSETRAP_ZONE}"

RESPONSE=$(curl -fsS -X POST "$MOUSETRAP_API_URL" \
    -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"zone\": \"$MOUSETRAP_ZONE\",
      \"name\": \"$NAME\",
      \"data\": \"$CERTBOT_VALIDATION\"
    }")

if [ "$RESPONSE" != "OK" ]; then
    echo "Error: API returned unexpected response: $RESPONSE" >&2
    exit 1
fi

# Warten auf DNS-Propagation
sleep 30
