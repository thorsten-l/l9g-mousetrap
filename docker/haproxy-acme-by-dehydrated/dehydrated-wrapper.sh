#!/bin/sh

set -eu

apk add --no-cache bash curl openssl git

if [ ! -d /dehydrated/.git ]; then
    git clone https://github.com/dehydrated-io/dehydrated.git /dehydrated
fi

mkdir -p /data/accounts /data/certs /data/chains /data/archive /certs
chmod +x /hooks/dns-hook.sh

COMMAND="${DEHYDRATED_COMMAND:-issue}"

cat > /data/config <<EOF
CA="https://acme-staging-v02.api.letsencrypt.org/directory"
CHALLENGETYPE="dns-01"
HOOK="/hooks/dns-hook.sh"
BASEDIR="/data"
DOMAINS_TXT="/domains.txt"
CONTACT_EMAIL="${ACME_EMAIL}"
WELLKNOWN="/tmp"
PRIVATE_KEY_RENEW="yes"
EOF

case "$COMMAND" in
    register)
        exec /dehydrated/dehydrated --register --accept-terms --config /data/config
        ;;
    issue)
        exec /dehydrated/dehydrated -c --config /data/config
        ;;
    renew)
        exec /dehydrated/dehydrated -c --config /data/config
        ;;
    *)
        echo "Unknown DEHYDRATED_COMMAND: $COMMAND" >&2
        exit 1
        ;;
esac
