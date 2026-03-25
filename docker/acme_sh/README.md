# acme.sh + Mousetrap DNS-01

Standalone [acme.sh](https://github.com/acmesh-official/acme.sh)-Konfiguration zur Zertifikatsausstellung via Mousetrap DNS-01 Hook — ohne gebundenen Webserver.

## Konzept

acme.sh nutzt den custom DNS-Hook `dns_mousetrap.sh`, der die `l9g-mousetrap`-API aufruft, um `_acme-challenge`-TXT-Records in Micetro zu setzen und zu löschen.

## Verzeichnisstruktur

```
.
├── acme-dns-hooks/
│   └── dns_mousetrap.sh              # acme.sh DNS-Hook für Mousetrap API
├── certs/                            # Zertifikate und acme.sh-State (git-ignored)
├── dot.env.sample                    # Beispiel-Umgebungsvariablen
├── docker-compose-daemon.yaml        # acme.sh als Daemon (für automatische Erneuerung)
├── docker-compose-oneshot.yaml       # acme.sh als einmaliger Task
├── START_ACME_ONESHOT.sh             # Zertifikat ausstellen (One-Shot)
├── START_ACME_ON_DAEMON_CONTAINER.sh # Zertifikat ausstellen im laufenden Daemon
└── FULLTEST_ONESHOT.sh               # Certs löschen und neu ausstellen
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
| `APP_DOMAIN`        | Domain für das Zertifikat                             |

## Betriebsmodi

### One-Shot (empfohlen für einfache Automatisierung)

Startet einen temporären Container, stellt das Zertifikat aus und entfernt den Container:

```bash
mkdir -p certs
./START_ACME_ONESHOT.sh
```

### Daemon (empfohlen für automatische Erneuerung)

Startet acme.sh als dauerhaft laufenden Dienst mit integriertem Renewal-Cron:

```bash
docker compose -f docker-compose-daemon.yaml up -d
./START_ACME_ON_DAEMON_CONTAINER.sh
```

### Volltest

```bash
./FULLTEST_ONESHOT.sh
```

## Zertifikate einbinden

Die ausgestellten Zertifikate liegen unter `./certs/<APP_DOMAIN>_ecc/`. Für die Einbindung in einen Webserver siehe die spezifischen Beispiele:

- `../haproxy-acme-by-acme_sh/` — HAProxy (kombiniertes PEM)
- `../nginx-acme-by-acme_sh/` — nginx (separate key + cert)
- `../nginx-proxy-acme-by-acme_sh/` — nginx als Reverse Proxy
- `../apache-httpd-acme-by-acme_sh/` — Apache HTTPD
- `../apache-httpd-proxy-acme-by-acme_sh/` — Apache HTTPD als Reverse Proxy
- `../tomcat-acme-by-acme_sh/` — Apache Tomcat
