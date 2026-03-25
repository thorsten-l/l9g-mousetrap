# l9g-mousetrap + Traefik

Integriertes Deployment von `l9g-mousetrap` zusammen mit Traefik als Reverse Proxy mit automatischem Zertifikatsmanagement via ACME DNS-01.

## Konzept

Traefik nutzt den `exec`-DNS-Provider von Lego. Bei einer Zertifikatsanfrage ruft Traefik `dns-hook.sh` auf, das seinerseits die `l9g-mousetrap`-API anspricht, um den `_acme-challenge`-TXT-Record in Micetro zu setzen bzw. zu löschen.

```
Traefik/Lego  →  dns-hook.sh  →  l9g-mousetrap API  →  Micetro DNS
```

## Voraussetzungen

Das JAR muss vor dem Start in dieses Verzeichnis kopiert werden:

```bash
./STARTUP.sh        # kopiert ../../target/l9g-mousetrap.jar und startet docker compose
```

## Verzeichnisstruktur

```
.
├── traefik/
│   ├── Dockerfile                    # traefik:v3.6 + curl + dns-hook.sh
│   └── dns-hook.sh                   # Lego-Hook: present/cleanup → Mousetrap API
├── acme/
│   └── acme.json                     # Persistente Zertifikatsspeicherung
├── data/
│   ├── config-sample.yaml
│   ├── config.yaml                   # Mousetrap-Konfiguration (nicht committen!)
│   └── secret.bin                    # Verschlüsselungsschlüssel (nicht committen!)
├── .env                              # Umgebungsvariablen
├── docker-compose.yaml
├── STARTUP.sh
├── GENERATE_TOKEN.sh
└── REFRESH_CONFIG.sh
```

## Konfiguration

| Variable             | Beschreibung                                          |
|----------------------|-------------------------------------------------------|
| `MOUSETRAP_API_URL`  | URL des Mousetrap-Service (intern: `http://l9g-mousetrap:8080/...`) |
| `MOUSETRAP_TOKEN`    | Bearer Token (base64-kodiert)                         |
| `MOUSETRAP_ZONE`     | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`         | Domain der zu schützenden Anwendung                   |
| `APP_ADMIN_EMAIL`    | E-Mail für ACME-Account-Registrierung                 |
| `TRAEFIK_PROFILE`    | Eindeutiger Bezeichner dieser Instanz (s.u.)          |

## Profil-Isolation

Über `TRAEFIK_PROFILE` können mehrere unabhängige Traefik-Instanzen auf demselben Docker-Host betrieben werden. Traefik ignoriert alle Container, die **nicht** das Label `traefik.profile=<TRAEFIK_PROFILE>` tragen:

```yaml
# Traefik-Constraint:
--providers.docker.constraints=Label(`traefik.profile`, `${TRAEFIK_PROFILE}`)

# Container-Label (muss gesetzt sein):
traefik.profile: ${TRAEFIK_PROFILE}
```

## Nutzung

```bash
# Erstkonfiguration (analog zu mousetrap/):
# 1. data/secret.bin initialisieren
# 2. data/config.yaml anlegen
# 3. Bearer Token generieren: ./GENERATE_TOKEN.sh

./STARTUP.sh
```

Traefik erkennt `l9g-mousetrap` über Docker-Labels und beantragt automatisch ein Zertifikat.

## Traefik-Image

`traefik/Dockerfile` baut ein Custom-Image auf Basis `traefik:v3.6` mit `curl` und dem eingebetteten `dns-hook.sh`. Das Image wird beim Start automatisch gebaut.

## Hinweis: Staging

Standardmäßig ist der Let's Encrypt **Staging**-Server aktiv. Für Produktionszertifikate die folgende Zeile in `docker-compose.yaml` entfernen:

```yaml
- "--certificatesresolvers.myresolver.acme.caserver=https://acme-staging-v02.api.letsencrypt.org/directory"
```
