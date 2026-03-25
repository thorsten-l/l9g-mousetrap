# Tomcat + acme.sh mit Mousetrap DNS-01

Standalone Apache Tomcat Webserver mit TLS-Zertifikaten von [acme.sh](https://github.com/acmesh-official/acme.sh) via Mousetrap DNS-01 Hook.

## Verzeichnisstruktur

```
.
├── acme-dns-hooks/dns_mousetrap.sh   # acme.sh DNS-Hook für Mousetrap API
├── certs/                            # Zertifikate und acme.sh-State (git-ignored)
├── html/                             # webapps/ROOT (eigene Inhalte hier ablegen)
├── .env                              # Umgebungsvariablen
├── docker-compose.yaml
├── server.xml                        # Tomcat-Konfiguration (HTTP 8080 + HTTPS 8443)
├── rewrite.config                    # HTTP→HTTPS Redirect (RewriteValve)
├── INSTALL_TOMCAT_CERTS.sh           # Kopiert acme.sh-Output nach site.key / site.cer
├── START_ACME_ONESHOT.sh             # Zertifikat ausstellen
├── START_ACME_RENEW.sh               # Zertifikat erneuern + Tomcat neu starten
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
| `TOMCAT_PROFILE`    | Suffix für Docker Container-Namen                     |

## Zertifikatsdateien

acme.sh legt Zertifikate unter `./certs/<APP_DOMAIN>_ecc/` ab. `INSTALL_TOMCAT_CERTS.sh` kopiert sie auf feste Pfade:

| Quelle                                     | Ziel             |
|--------------------------------------------|------------------|
| `certs/<APP_DOMAIN>_ecc/fullchain.cer`    | `certs/site.cer` |
| `certs/<APP_DOMAIN>_ecc/<APP_DOMAIN>.key` | `certs/site.key` |

## Nutzung

### Zertifikat ausstellen (erstmalig)

```bash
mkdir -p certs html
./START_ACME_ONESHOT.sh
docker compose up -d tomcat
```

### Zertifikat erneuern

```bash
./START_ACME_RENEW.sh
```

Tomcat liest Zertifikate beim Start ein — nach der Erneuerung wird der Container neu gestartet (`docker compose restart tomcat`).

### Volltest (Certs löschen + neu ausstellen)

```bash
./FULLTEST_ONESHOT.sh
```

## Tomcat-Konfiguration

`server.xml` konfiguriert zwei Connectors:

- **Port 8080** (HTTP, mapped auf 80): HTTP-Anfragen werden per `RewriteValve` auf HTTPS umgeleitet
- **Port 8443** (HTTPS, mapped auf 443): NIO-Connector mit PEM-Zertifikaten (Tomcat 8.5+)

`rewrite.config` liegt unter `conf/Catalina/localhost/rewrite.config` und wird von der `RewriteValve` im `<Host>`-Element gelesen.

## Eigene Inhalte

Dateien im Verzeichnis `./html/` werden als `webapps/ROOT` direkt von Tomcat unter `https://<APP_DOMAIN>/` ausgeliefert. WARs können alternativ in ein separates `webapps/`-Volume gelegt werden.
