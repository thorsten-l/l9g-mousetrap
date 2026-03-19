#!/usr/bin/env sh

# Hook script for l9g-mousetrap
# Env vars required:
# - MOUSETRAP_API_URL
# - MOUSETRAP_TOKEN
# - MOUSETRAP_ZONE

dns_mousetrap_add() {
  fulldomain=$1
  txtvalue=$2
  
  _info "Mousetrap: Adding record for $fulldomain"

  # Simple removal of the zone from the FQDN
  # We rely on MOUSETRAP_ZONE being correctly set in the .env
  name="${fulldomain%.$MOUSETRAP_ZONE}"

  # API Call
  response=$(curl -s -X POST "$MOUSETRAP_API_URL" \
    -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"zone\": \"$MOUSETRAP_ZONE\", 
      \"name\": \"$name\", 
      \"data\": \"$txtvalue\"
    }")

  if echo "$response" | grep -q "error"; then
    _err "Mousetrap API Error: $response"
    return 1
  fi

  return 0
}

dns_mousetrap_rm() {
  fulldomain=$1
  txtvalue=$2
  
  _info "Mousetrap: Removing record for $fulldomain"

  name="${fulldomain%.$MOUSETRAP_ZONE}"

  curl -s -X DELETE "$MOUSETRAP_API_URL" \
    -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"zone\": \"$MOUSETRAP_ZONE\", 
      \"name\": \"$name\", 
      \"data\": \"$txtvalue\"
    }"
    
  return 0
}