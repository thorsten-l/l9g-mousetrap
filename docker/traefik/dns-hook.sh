#!/bin/sh

# Argumente von Traefik/Lego:
# $1 = action ('present' oder 'cleanup')
# $2 = fqdn (z.B. '_acme-challenge.test2.dev.example.de.')
# $3 = value (Der Token für den TXT Record)

# Umgebungsvariablen prüfen
if [ -z "$MICETRO_ZONE" ] || [ -z "$MICETRO_TOKEN" ] || [ -z "$MICETRO_API_URL" ]; then
  echo "Error: MICETRO_ZONE, MICETRO_TOKEN or MICETRO_API_URL environment variable is missing."
  exit 1
fi

# Wir nutzen den FQDN direkt so, wie er kommt.
# Die API kümmert sich um das Parsen/Zuordnen zur Zone.
FQDN="$2"

if [ "$1" = "present" ]; then
    # Erstellen
    # Wir übergeben die Zone (für den Kontext) und den vollen Namen ($FQDN)
    curl -s -X POST "$MICETRO_API_URL" \
      -H "Authorization: Bearer $MICETRO_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MICETRO_ZONE\", \"name\": \"$FQDN\", \"data\": \"$3\"}"

elif [ "$1" = "cleanup" ]; then
    # Löschen
    curl -s -X DELETE "$MICETRO_API_URL" \
      -H "Authorization: Bearer $MICETRO_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"zone\": \"$MICETRO_ZONE\", \"name\": \"$FQDN\"}"
fi
