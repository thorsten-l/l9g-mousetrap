#!/usr/bin/env sh

set -eu

. ./.env

DOMAIN_DIR="./certs/${APP_DOMAIN}_ecc"

KEY_FILE="${DOMAIN_DIR}/${APP_DOMAIN}.key"
FULLCHAIN_FILE="${DOMAIN_DIR}/fullchain.cer"

if [ ! -f "$KEY_FILE" ]; then
    echo "Key file not found: $KEY_FILE" >&2
    exit 1
fi

if [ ! -f "$FULLCHAIN_FILE" ]; then
    echo "Fullchain file not found: $FULLCHAIN_FILE" >&2
    exit 1
fi

cp "$KEY_FILE" "./certs/site.key"
cp "$FULLCHAIN_FILE" "./certs/site.cer"
chmod 600 "./certs/site.key"

echo "Apache certs installed to ./certs/site.key and ./certs/site.cer"
