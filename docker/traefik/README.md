# Traefik + Mousetrap DNS-01

Traefik als Reverse Proxy mit automatischem Zertifikatsmanagement via ACME DNS-01 und Mousetrap.

## Konzept

Traefik nutzt den `exec`-DNS-Provider von Lego. Bei einer Zertifikatsanfrage ruft Traefik `dns-hook.sh` auf, das die `l9g-mousetrap`-API anspricht, um den `_acme-challenge`-TXT-Record in Micetro zu verwalten.

```
Traefik/Lego  →  dns-hook.sh  →  l9g-mousetrap API  →  Micetro DNS
```

## Verzeichnisstruktur

```
.
├── Dockerfile                        # traefik:v3.6 + curl + dns-hook.sh
├── dns-hook.sh                       # Lego-Hook: present/cleanup → Mousetrap API
├── docker-compose.yaml               # Traefik + webapp (nginx)
├── dot.env.sample                    # Beispiel-Umgebungsvariablen
└── letsencrypt/
    └── acme.json                     # Persistente Zertifikatsspeicherung (chmod 600)
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
| `APP_DOMAIN`        | Domain der zu schützenden Anwendung                   |
| `TRAEFIK_PROFILE`   | Eindeutiger Bezeichner dieser Instanz (s.u.)          |

## Profil-Isolation

Über `TRAEFIK_PROFILE` können mehrere unabhängige Traefik-Instanzen auf demselben Docker-Host betrieben werden. Traefik ignoriert alle Container ohne passendes Label:

```yaml
# Traefik-Constraint:
--providers.docker.constraints=Label(`traefik.profile`, `${TRAEFIK_PROFILE}`)

# Container-Label (muss an jedem verwalteten Container gesetzt sein):
traefik.profile: ${TRAEFIK_PROFILE}
```

## Nutzung

```bash
docker compose up -d
```

Traefik erkennt den `webapp`-Container über Docker-Labels und beantragt automatisch ein Zertifikat für `APP_DOMAIN`.

## Traefik-Image

`Dockerfile` baut ein Custom-Image auf Basis `traefik:v3.6` mit `curl` und eingebettetem `dns-hook.sh`. Das Image wird beim Start automatisch gebaut.

## Hinweis: Staging

Standardmäßig ist der Let's Encrypt **Staging**-Server aktiv. Für Produktionszertifikate diese Zeile in `docker-compose.yaml` entfernen:

```yaml
- "--certificatesresolvers.myresolver.acme.caserver=https://acme-staging-v02.api.letsencrypt.org/directory"
```

## acme.json

Die Datei `letsencrypt/acme.json` muss die Berechtigung `600` haben, sonst verweigert Traefik den Start:

```bash
chmod 600 letsencrypt/acme.json
```
