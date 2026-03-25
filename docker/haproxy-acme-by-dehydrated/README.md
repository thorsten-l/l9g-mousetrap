# HAProxy + dehydrated mit Mousetrap DNS-01

HAProxy als Reverse Proxy mit TLS-Zertifikaten von [dehydrated](https://github.com/dehydrated-io/dehydrated) via Mousetrap DNS-01 Hook.

## Verzeichnisstruktur

```
.
├── dns-hook.sh                       # dehydrated DNS-Hook für Mousetrap API
├── dehydrated-wrapper.sh             # Installiert dehydrated + startet Ausstellung/Erneuerung
├── domains.txt                       # Liste der zu zertifizierenden Domains
├── dot.env.sample                    # Beispiel-Umgebungsvariablen
├── dehydrated/                       # dehydrated-Quellcode (git-geclont beim Start)
├── dehydrated-data/                  # dehydrated-State (Accounts, Certs, git-ignored)
├── certs/                            # HAProxy PEM-Datei (git-ignored)
├── docker-compose.yaml               # haproxy + dehydrated (Alpine) + webapp
├── haproxy.cfg                       # HAProxy-Konfiguration (HTTP→HTTPS + SSL + Proxy)
└── FIRST_STARTUP.sh                  # Erststart: Account registrieren + Zertifikat ausstellen
```

## Konfiguration

```bash
cp dot.env.sample .env
```

| Variable            | Beschreibung                                          |
|---------------------|-------------------------------------------------------|
| `MOUSETRAP_API_URL` | URL des Mousetrap-Service                             |
| `MOUSETRAP_TOKEN`   | Bearer Token (base64-kodiert)                         |
| `MOUSETRAP_ZONE`    | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`        | Domain für das Zertifikat (auch in `domains.txt`)     |
| `ACME_EMAIL`        | E-Mail für ACME-Account-Registrierung                 |
| `HAPROXY_PROFILE`   | Suffix für Docker Container-Namen                     |

## Besonderheit: integriertes deploy_cert

Im Gegensatz zu acme.sh erstellt `dns-hook.sh` das kombinierte HAProxy-PEM direkt im `deploy_cert`-Hook:

```
/data/certs/<domain>/privkey.pem  ┐
/data/certs/<domain>/fullchain.pem┘  → /certs/site.pem
```

Das separate `BUILD_HAPROXY_PEM.sh`-Skript entfällt damit.

## dehydrated-Image

dehydrated läuft auf einem schlanken `alpine:3.20`-Container. `dehydrated-wrapper.sh` wird als Entrypoint ausgeführt und:
1. Installiert `bash`, `curl`, `openssl`, `git` via `apk`
2. Klont dehydrated von GitHub (falls nicht vorhanden)
3. Schreibt eine `/data/config` basierend auf den Umgebungsvariablen
4. Führt den per `DEHYDRATED_COMMAND` gewählten Befehl aus (`register`, `issue`, `renew`)

## Nutzung

### Erststart

```bash
cp dot.env.sample .env
# APP_DOMAIN in domains.txt eintragen
mkdir -p certs dehydrated-data
./FIRST_STARTUP.sh
```

`FIRST_STARTUP.sh` registriert zuerst den ACME-Account (`register`), stellt dann das Zertifikat aus (`issue`) und startet anschließend den Stack im Hintergrund.

### Zertifikat erneuern

```bash
docker compose run --rm -e DEHYDRATED_COMMAND=renew dehydrated
docker compose restart haproxy
```
