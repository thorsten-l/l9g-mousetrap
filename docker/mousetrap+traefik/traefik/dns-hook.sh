#!/bin/sh

# Arguments from Traefik/Lego:
# $1 = action ('present' or 'cleanup')
# $2 = fqdn (e.g., '_acme-challenge.test2.dev.example.de.')
# $3 = value (The token for the TXT record)

# Check environment variables
if [ -z "$MICETRO_ZONE" ] || [ -z "$MICETRO_TOKEN" ] || [ -z "$MICETRO_API_URL" ]; then
  echo "Error: MICETRO_ZONE, MICETRO_TOKEN or MICETRO_API_URL environment variable is missing."
  exit 1
fi

# We use the FQDN exactly as provided.
# The API handles parsing/mapping to the zone.
FQDN="$2"

if [ "$1" = "present" ]; then
    # Create record
    # We pass the zone (for context) and the full name ($FQDN)
    curl -s -X POST "$MICETRO_API_URL" \
      -H "Authorization: Bearer $MICETRO_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MICETRO_ZONE\", \"name\": \"$FQDN\", \"data\": \"$3\"}"

elif [ "$1" = "cleanup" ]; then
    # Delete record
    curl -s -X DELETE "$MICETRO_API_URL" \
      -H "Authorization: Bearer $MICETRO_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MICETRO_ZONE\", \"name\": \"$FQDN\"}"
fi
