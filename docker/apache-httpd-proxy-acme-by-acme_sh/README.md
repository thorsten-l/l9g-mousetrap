# Apache HTTPD + acme.sh with Mousetrap DNS-01

This example sets up **Apache HTTPD** as a reverse proxy with TLS certificates issued via [acme.sh](https://github.com/acmesh-official/acme.sh) using the Mousetrap DNS-01 hook.

## Directory Structure

```
.
├── acme-dns-hooks/dns_mousetrap.sh   # acme.sh DNS hook for Mousetrap API
├── certs/                            # Certificates and acme.sh state (git-ignored)
├── .env                              # Environment variables
├── docker-compose.yaml
├── httpd.conf                        # Apache config (HTTP→HTTPS + SSL + proxy)
├── INSTALL_APACHE_CERTS.sh           # Copies acme.sh output to site.key / site.cer
├── START_ACME_ONESHOT.sh             # Issue certificate (one-shot)
├── START_ACME_RENEW.sh               # Renew certificate + graceful reload of Apache
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
| `APACHE_PROFILE`    | Suffix for Docker container names                |

## Certificate Files

acme.sh stores certificates under `./certs/<APP_DOMAIN>_ecc/`. `INSTALL_APACHE_CERTS.sh` copies them to fixed paths that Apache expects:

| Source                                     | Target           |
|--------------------------------------------|------------------|
| `certs/<APP_DOMAIN>_ecc/fullchain.cer`    | `certs/site.cer` |
| `certs/<APP_DOMAIN>_ecc/<APP_DOMAIN>.key` | `certs/site.key` |

## Usage

### Issue certificate (first time)

```bash
mkdir -p certs
./START_ACME_ONESHOT.sh
docker compose up -d apache webapp
```

### Renew certificate

```bash
./START_ACME_RENEW.sh
```

Renew performs a graceful reload via `httpd -k graceful` — no downtime.

### Full test (wipe + re-issue)

```bash
./FULLTEST_ONESHOT.sh
```

## Apache Configuration

`httpd.conf` loads the required modules (`mod_ssl`, `mod_proxy`, `mod_proxy_http`, `mod_rewrite`, `mod_headers`) and configures:

- **Port 80**: permanent redirect (301) to HTTPS
- **Port 443**: SSL termination + reverse proxy to `webapp`

`SSLCertificateFile` and `SSLCertificateKeyFile` point to `/usr/local/apache2/certs/site.cer` and `site.key`, which are mounted from `./certs`.
