#!/bin/sh

# Arguments from Traefik/Lego:
# $1 = action ('present' or 'cleanup')
# $2 = fqdn (e.g., '_acme-challenge.test2.dev.example.de.')
# $3 = value (The token for the TXT record)

# Check environment variables
if [ -z "$MOUSETRAP_ZONE" ] || [ -z "$MOUSETRAP_TOKEN" ] || [ -z "$MOUSETRAP_API_URL" ]; then
  echo "Error: MOUSETRAP_ZONE, MOUSETRAP_TOKEN or MOUSETRAP_API_URL environment variable is missing."
  exit 1
fi

# We use the FQDN exactly as provided.
# The API handles parsing/mapping to the zone.
FQDN="$2"

if [ "$1" = "present" ]; then
    # Create record
    # We pass the zone (for context) and the full name ($FQDN)
    curl -s -X POST "$MOUSETRAP_API_URL" \
      -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MOUSETRAP_ZONE\", \"name\": \"$FQDN\", \"data\": \"$3\"}"

elif [ "$1" = "cleanup" ]; then
    # Delete record
    curl -s -X DELETE "$MOUSETRAP_API_URL" \
      -H "Authorization: Bearer $MOUSETRAP_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MOUSETRAP_ZONE\", \"name\": \"$FQDN\"}"
fi
