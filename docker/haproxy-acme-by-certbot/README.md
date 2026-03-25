# HAProxy + certbot mit Mousetrap DNS-01

HAProxy als Reverse Proxy mit TLS-Zertifikaten von [certbot](https://certbot.eff.org/) via Mousetrap DNS-01 Hook.

## Verzeichnisstruktur

```
.
├── certbot-hooks/
│   ├── mousetrap-auth.sh             # certbot Auth-Hook: erstellt TXT-Record via Mousetrap
│   └── mousetrap-cleanup.sh          # certbot Cleanup-Hook: löscht TXT-Record via Mousetrap
├── certbot-data/                     # certbot-Daten und ausgestellte Zertifikate (git-ignored)
├── certs/                            # HAProxy PEM-Datei (git-ignored)
├── .env                              # Umgebungsvariablen
├── docker-compose.yaml               # haproxy + certbot + webapp
├── Dockerfile.certbot                # Alpine + certbot + curl
├── haproxy.cfg                       # HAProxy-Konfiguration (HTTP→HTTPS + SSL + Proxy)
├── BUILD_HAPROXY_PEM_CERTBOT.sh      # Kombiniert privkey + fullchain zu site.pem
├── START_CERTBOT_ONESHOT.sh          # Zertifikat ausstellen
├── START_CERTBOT_RENEW.sh            # Zertifikat erneuern + HAProxy neu starten
└── FULLTEST_ONESHOT.sh               # Daten löschen und neu ausstellen
```

## Konfiguration

| Variable            | Beschreibung                                          |
|---------------------|-------------------------------------------------------|
| `MOUSETRAP_API_URL` | URL des Mousetrap-Service                             |
| `MOUSETRAP_TOKEN`   | Bearer Token (base64-kodiert)                         |
| `MOUSETRAP_ZONE`    | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`        | Domain für das Zertifikat                             |
| `ACME_EMAIL`        | E-Mail für certbot-Account-Registrierung              |
| `HAPROXY_PROFILE`   | Suffix für Docker Container-Namen                     |

## Zertifikatsdateien

certbot legt Zertifikate unter `./certbot-data/live/<APP_DOMAIN>/` ab.
`BUILD_HAPROXY_PEM_CERTBOT.sh` erstellt `./certs/site.pem`:

```
certbot-data/live/<APP_DOMAIN>/privkey.pem   ┐
certbot-data/live/<APP_DOMAIN>/fullchain.pem ┘  → certs/site.pem
```

## Besonderheit: certbot-Hooks

Im Gegensatz zu acme.sh verwendet certbot eigene Auth- und Cleanup-Hooks:

- `mousetrap-auth.sh`: Wird von certbot mit `CERTBOT_DOMAIN` und `CERTBOT_VALIDATION` aufgerufen. Erstellt den `_acme-challenge`-TXT-Record und wartet 30 Sekunden auf DNS-Propagation.
- `mousetrap-cleanup.sh`: Löscht den TXT-Record nach erfolgter Validierung.

## Nutzung

### Zertifikat ausstellen (erstmalig)

```bash
mkdir -p certbot-data certs
./START_CERTBOT_ONESHOT.sh
docker compose up -d haproxy webapp
```

### Zertifikat erneuern

```bash
./START_CERTBOT_RENEW.sh
```

### Volltest (Daten löschen + neu ausstellen)

```bash
./FULLTEST_ONESHOT.sh
```

## certbot-Image

`Dockerfile.certbot` baut ein minimales Alpine-Image mit `certbot` und `curl`. Das Image wird beim ersten `docker compose run` automatisch gebaut.
