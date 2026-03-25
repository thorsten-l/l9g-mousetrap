# Nginx + acme.sh with Mousetrap DNS-01

This example sets up **nginx** as a reverse proxy with TLS certificates issued via [acme.sh](https://github.com/acmesh-official/acme.sh) using the Mousetrap DNS-01 hook.

## Directory Structure

```
.
├── acme-dns-hooks/dns_mousetrap.sh   # acme.sh DNS hook for Mousetrap API
├── certs/                            # Certificates and acme.sh state (git-ignored)
├── .env                              # Environment variables
├── docker-compose.yaml
├── nginx.conf                        # nginx reverse proxy config (HTTP→HTTPS + SSL)
├── INSTALL_NGINX_CERTS.sh            # Copies acme.sh output to site.key / site.cer
├── START_ACME_ONESHOT.sh             # Issue certificate (one-shot)
├── START_ACME_RENEW.sh               # Renew certificate + reload nginx
└── FULLTEST_ONESHOT.sh               # Full test: wipe certs and re-issue
```

## Configuration

Copy and edit the environment file:

```bash
cp .env .env.local   # optional, or edit .env directly
```

| Variable            | Description                                      |
|---------------------|--------------------------------------------------|
| `MOUSETRAP_API_URL` | URL of the Mousetrap service                     |
| `MOUSETRAP_TOKEN`   | Bearer token (base64-encoded)                    |
| `MOUSETRAP_ZONE`    | DNS zone with trailing dot, e.g. `example.de.`  |
| `APP_DOMAIN`        | Domain to request the certificate for            |
| `ACME_EMAIL`        | E-mail for ACME account registration             |
| `NGINX_PROFILE`     | Suffix for Docker container names                |

## Certificate Files

acme.sh stores certificates under `./certs/<APP_DOMAIN>_ecc/`. `INSTALL_NGINX_CERTS.sh` copies them to fixed paths that nginx expects:

| Source                                   | Target           |
|------------------------------------------|------------------|
| `certs/<APP_DOMAIN>_ecc/fullchain.cer`  | `certs/site.cer` |
| `certs/<APP_DOMAIN>_ecc/<APP_DOMAIN>.key` | `certs/site.key` |

## Usage

### Issue certificate (first time)

```bash
mkdir -p certs
./START_ACME_ONESHOT.sh
docker compose up -d nginx webapp
```

### Renew certificate

```bash
./START_ACME_RENEW.sh
```

Renew reloads nginx in-place via `nginx -s reload` — no downtime.

### Full test (wipe + re-issue)

```bash
./FULLTEST_ONESHOT.sh
```

## nginx Configuration

`nginx.conf` listens on port 80 (redirect to HTTPS) and 443 (SSL termination + proxy to `webapp`). Replace `server_name _;` with your actual domain if needed.
