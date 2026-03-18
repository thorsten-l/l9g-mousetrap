#!/usr/bin/env sh

# Hook script for dns_mousetrap
# Env vars required:
# - MICETRO_API_URL
# - MICETRO_TOKEN
# - MICETRO_ZONE

dns_mousetrap_add() {
    fulldomain="$1"
    txtvalue="$2"

    _info "Mousetrap: Adding record for $fulldomain"

    name="${fulldomain%.$MICETRO_ZONE}"

    response=$(curl -fsS -X POST "$MICETRO_API_URL" \
        -H "Authorization: Bearer $MICETRO_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
          \"zone\": \"$MICETRO_ZONE\",
          \"name\": \"$name\",
          \"data\": \"$txtvalue\"
        }")

    if echo "$response" | grep -qi "error"; then
        _err "Mousetrap API Error: $response"
        return 1
    fi

    return 0
}

dns_mousetrap_rm() {
    fulldomain="$1"
    txtvalue="$2"

    _info "Mousetrap: Removing record for $fulldomain"

    name="${fulldomain%.$MICETRO_ZONE}"

    curl -fsS -X DELETE "$MICETRO_API_URL" \
        -H "Authorization: Bearer $MICETRO_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
          \"zone\": \"$MICETRO_ZONE\",
          \"name\": \"$name\",
          \"data\": \"$txtvalue\"
        }"

    return 0
}
