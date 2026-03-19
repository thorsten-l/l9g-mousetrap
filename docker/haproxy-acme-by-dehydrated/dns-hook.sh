#!/bin/sh

set -eu

if [ -z "${MOUSETRAP_ZONE:-}" ] || [ -z "${MOUSETRAP_TOKEN:-}" ] || [ -z "${MOUSETRAP_API_URL:-}" ]; then
    echo "Error: MOUSETRAP_ZONE, MOUSETRAP_TOKEN or MOUSETRAP_API_URL environment variable is missing." >&2
    exit 1
fi

ACTION="$1"

case "$ACTION" in
    deploy_challenge)
        DOMAIN="$2"
        TOKEN_FILENAME="$3"
        TOKEN_VALUE="$4"

        case "$DOMAIN" in
            _acme-challenge.*)
                FQDN="$DOMAIN"
                ;;
            *)
                FQDN="_acme-challenge.$DOMAIN"
                ;;
        esac

        echo "dns-hook: deploy_challenge domain=$DOMAIN fqdn=$FQDN" >&2

        curl -fsS -X POST "$MOUSETRAP_API_URL" \
            -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
            -H "Content-Type: application/json" \
            -d "{\"zone\": \"$MOUSETRAP_ZONE\", \"name\": \"$FQDN\", \"data\": \"$TOKEN_VALUE\"}"

        sleep 30
        ;;

    clean_challenge)
        DOMAIN="$2"
        TOKEN_FILENAME="$3"
        TOKEN_VALUE="$4"

        case "$DOMAIN" in
            _acme-challenge.*)
                FQDN="$DOMAIN"
                ;;
            *)
                FQDN="_acme-challenge.$DOMAIN"
                ;;
        esac

        echo "dns-hook: clean_challenge domain=$DOMAIN fqdn=$FQDN" >&2

        curl -fsS -X DELETE "$MOUSETRAP_API_URL" \
            -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
            -H "Content-Type: application/json" \
            -d "{\"zone\": \"$MOUSETRAP_ZONE\", \"name\": \"$FQDN\"}"
        ;;

    deploy_cert)
        DOMAIN="$2"
        KEYFILE="$3"
        CERTFILE="$4"
        FULLCHAINFILE="$5"
        CHAINFILE="$6"
        TIMESTAMP="$7"

        echo "dns-hook: deploy_cert domain=$DOMAIN" >&2

        cat "$KEYFILE" "$FULLCHAINFILE" > /certs/site.pem
        chmod 600 /certs/site.pem
        ;;

    unchanged_cert)
        DOMAIN="$2"
        KEYFILE="$3"
        CERTFILE="$4"
        FULLCHAINFILE="$5"
        CHAINFILE="$6"

        echo "dns-hook: unchanged_cert domain=$DOMAIN" >&2

        cat "$KEYFILE" "$FULLCHAINFILE" > /certs/site.pem
        chmod 600 /certs/site.pem
        ;;

    invalid_challenge|request_failure|startup_hook|exit_hook|generate_csr|sync_cert)
        echo "dns-hook: ignoring action $ACTION" >&2
        ;;

    *)
        echo "dns-hook: ignoring unknown action $ACTION" >&2
        exit 0
        ;;
esac
