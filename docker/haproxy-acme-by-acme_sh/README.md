# HAProxy + acme.sh mit Mousetrap DNS-01

HAProxy als Reverse Proxy mit TLS-Zertifikaten von [acme.sh](https://github.com/acmesh-official/acme.sh) via Mousetrap DNS-01 Hook.

## Verzeichnisstruktur

```
.
├── acme-dns-hooks/dns_mousetrap.sh   # acme.sh DNS-Hook für Mousetrap API
├── certs/                            # Zertifikate und acme.sh-State (git-ignored)
├── .env                              # Umgebungsvariablen
├── docker-compose.yaml               # haproxy + acme + webapp
├── haproxy.cfg                       # HAProxy-Konfiguration (HTTP→HTTPS + SSL + Proxy)
├── BUILD_HAPROXY_PEM.sh              # Kombiniert key + fullchain zu site.pem
├── START_ACME_ONESHOT.sh             # Zertifikat ausstellen
├── START_ACME_RENEW.sh               # Zertifikat erneuern + HAProxy neu starten
└── FULLTEST_ONESHOT.sh               # Certs löschen und neu ausstellen
```

## Konfiguration

| Variable            | Beschreibung                                          |
|---------------------|-------------------------------------------------------|
| `MOUSETRAP_API_URL` | URL des Mousetrap-Service                             |
| `MOUSETRAP_TOKEN`   | Bearer Token (base64-kodiert)                         |
| `MOUSETRAP_ZONE`    | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`        | Domain für das Zertifikat                             |
| `ACME_EMAIL`        | E-Mail für ACME-Account-Registrierung                 |
| `HAPROXY_PROFILE`   | Suffix für Docker Container-Namen                     |

## Zertifikatsdateien

HAProxy benötigt ein kombiniertes PEM aus Private Key und Full Chain.
`BUILD_HAPROXY_PEM.sh` erstellt `./certs/site.pem`:

```
certs/<APP_DOMAIN>_ecc/<APP_DOMAIN>.key  ┐
certs/<APP_DOMAIN>_ecc/fullchain.cer     ┘  → certs/site.pem
```

## Nutzung

### Zertifikat ausstellen (erstmalig)

```bash
mkdir -p certs
./START_ACME_ONESHOT.sh
docker compose up -d haproxy webapp
```

### Zertifikat erneuern

```bash
./START_ACME_RENEW.sh
```

Erneuert das Zertifikat, baut `site.pem` neu und startet HAProxy neu.

### Volltest (Certs löschen + neu ausstellen)

```bash
./FULLTEST_ONESHOT.sh
```

## HAProxy-Konfiguration

`haproxy.cfg` konfiguriert:

- **Frontend `fe_http` (Port 80)**: HTTP-Anfragen werden per `redirect scheme https` auf HTTPS umgeleitet
- **Frontend `fe_https` (Port 443)**: SSL-Termination mit `site.pem`, HTTP/2 + HTTP/1.1 via ALPN
- **Backend `be_app`**: Weiterleitung an den `webapp`-Container (nginx:alpine)
