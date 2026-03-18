#!/usr/bin/env sh

set -eu

. ./.env

DOMAIN_DIR="./certs/${APP_DOMAIN}_ecc"

KEY_FILE="${DOMAIN_DIR}/${APP_DOMAIN}.key"
FULLCHAIN_FILE="${DOMAIN_DIR}/fullchain.cer"
TARGET_PEM="./certs/site.pem"

if [ ! -f "$KEY_FILE" ]; then
    echo "Key file not found: $KEY_FILE" >&2
    exit 1
fi

if [ ! -f "$FULLCHAIN_FILE" ]; then
    echo "Fullchain file not found: $FULLCHAIN_FILE" >&2
    exit 1
fi

cat "$KEY_FILE" "$FULLCHAIN_FILE" > "$TARGET_PEM"
chmod 600 "$TARGET_PEM"

echo "HAProxy PEM written to $TARGET_PEM"
