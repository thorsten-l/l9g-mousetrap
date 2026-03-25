# l9g-mousetrap — Standalone Service

Deployment-Konfiguration für den `l9g-mousetrap`-Dienst als eigenständigen Docker-Container.

## Voraussetzungen

Das JAR muss vor dem Start in dieses Verzeichnis kopiert werden:

```bash
./STARTUP.sh        # kopiert ../../target/l9g-mousetrap.jar und startet docker compose
```

Oder manuell:

```bash
cp ../../target/l9g-mousetrap.jar .
docker compose up -d
```

## Verzeichnisstruktur

```
.
├── data/
│   ├── config-sample.yaml            # Beispielkonfiguration
│   ├── config.yaml                   # Aktive Konfiguration (nicht committen!)
│   └── secret.bin                    # Verschlüsselungsschlüssel (nicht committen!)
├── docker-compose.yaml
├── STARTUP.sh                        # JAR kopieren + Stack starten
├── GENERATE_TOKEN.sh                 # Neuen Bearer Token erzeugen
└── REFRESH_CONFIG.sh                 # Konfiguration zur Laufzeit neu laden
```

## Erstkonfiguration

### 1. Verschlüsselungsschlüssel anlegen

```bash
docker run --rm \
  -v ./data:/data \
  -v ./l9g-mousetrap.jar:/l9g-mousetrap.jar \
  bellsoft/liberica-openjdk-alpine:21 \
  java -jar /l9g-mousetrap.jar -i
```

### 2. Passwörter und Tokens verschlüsseln

```bash
docker run --rm \
  -v ./data:/data:ro \
  -v ./l9g-mousetrap.jar:/l9g-mousetrap.jar:ro \
  bellsoft/liberica-openjdk-alpine:21 \
  java -jar /l9g-mousetrap.jar -e "mein-klartext-wert"
```

### 3. Bearer Token generieren

```bash
./GENERATE_TOKEN.sh
```

### 4. `data/config.yaml` anlegen

Vorlage aus `data/config-sample.yaml` kopieren und anpassen:

```yaml
micetro:
  api-url: "https://mm.example.de/mmws/api/v2/JSON"
  server: "localhost"
  login-name: "apiuser"
  password: "{AES256}..."          # verschlüsselt mit -e
  session-cache-ttl: 245

bearer-tokens:
  map:
    mein-token:
      token: "{AES256}..."         # verschlüsselt mit -e
      owner: name
      description: Beschreibung
      permitted-zones:
        - example.de.
      enabled: true
```

## Laufzeitbetrieb

### Konfiguration neu laden (ohne Neustart)

```bash
./REFRESH_CONFIG.sh
```

Ruft den Spring-Actuator-Endpoint `POST /actuator/refresh` auf Port 9000 auf.

## Ports

| Port | Beschreibung          |
|------|-----------------------|
| 8080 | REST API              |
| 9000 | Actuator (intern)     |
