# nginx + acme.sh mit Mousetrap DNS-01

Standalone nginx-Webserver mit TLS-Zertifikaten von [acme.sh](https://github.com/acmesh-official/acme.sh) via Mousetrap DNS-01 Hook.

## Verzeichnisstruktur

```
.
├── acme-dns-hooks/dns_mousetrap.sh   # acme.sh DNS-Hook für Mousetrap API
├── certs/                            # Zertifikate und acme.sh-State (git-ignored)
├── html/                             # Document Root (eigene Inhalte hier ablegen)
├── .env                              # Umgebungsvariablen
├── docker-compose.yaml
├── nginx.conf                        # HTTP→HTTPS Redirect + SSL + statische Dateien
├── INSTALL_NGINX_CERTS.sh            # Kopiert acme.sh-Output nach site.key / site.cer
├── START_ACME_ONESHOT.sh             # Zertifikat ausstellen
├── START_ACME_RENEW.sh               # Zertifikat erneuern + nginx reload
└── FULLTEST_ONESHOT.sh               # Certs löschen und neu ausstellen
```

## Konfiguration

| Variable            | Beschreibung                                       |
|---------------------|----------------------------------------------------|
| `MOUSETRAP_API_URL` | URL des Mousetrap-Service                          |
| `MOUSETRAP_TOKEN`   | Bearer Token (base64-kodiert)                      |
| `MOUSETRAP_ZONE`    | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`        | Domain für das Zertifikat                          |
| `ACME_EMAIL`        | E-Mail für ACME-Account-Registrierung              |
| `NGINX_PROFILE`     | Suffix für Docker Container-Namen                  |

## Zertifikatsdateien

acme.sh legt Zertifikate unter `./certs/<APP_DOMAIN>_ecc/` ab. `INSTALL_NGINX_CERTS.sh` kopiert sie auf feste Pfade:

| Quelle                                     | Ziel             |
|--------------------------------------------|------------------|
| `certs/<APP_DOMAIN>_ecc/fullchain.cer`    | `certs/site.cer` |
| `certs/<APP_DOMAIN>_ecc/<APP_DOMAIN>.key` | `certs/site.key` |

## Nutzung

### Zertifikat ausstellen (erstmalig)

```bash
mkdir -p certs html
./START_ACME_ONESHOT.sh
docker compose up -d nginx
```

### Zertifikat erneuern

```bash
./START_ACME_RENEW.sh
```

Führt `nginx -s reload` aus — kein Downtime.

### Volltest (Certs löschen + neu ausstellen)

```bash
./FULLTEST_ONESHOT.sh
```

## Eigene Inhalte

Dateien im Verzeichnis `./html/` werden direkt von nginx unter `https://<APP_DOMAIN>/` ausgeliefert.
