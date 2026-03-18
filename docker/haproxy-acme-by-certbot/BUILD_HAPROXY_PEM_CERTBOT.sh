#!/usr/bin/env sh

set -eu

. ./.env

DOMAIN_DIR="./certbot-data/live/${APP_DOMAIN}"

PRIVKEY_FILE="${DOMAIN_DIR}/privkey.pem"
FULLCHAIN_FILE="${DOMAIN_DIR}/fullchain.pem"
TARGET_PEM="./certs/site.pem"

if [ ! -f "$PRIVKEY_FILE" ]; then
    echo "Private key not found: $PRIVKEY_FILE" >&2
    exit 1
fi

if [ ! -f "$FULLCHAIN_FILE" ]; then
    echo "Fullchain not found: $FULLCHAIN_FILE" >&2
    exit 1
fi

cat "$PRIVKEY_FILE" "$FULLCHAIN_FILE" > "$TARGET_PEM"
chmod 600 "$TARGET_PEM"

echo "HAProxy PEM written to $TARGET_PEM"
