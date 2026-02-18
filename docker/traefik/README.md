# Traefik Integration with Mousetrap DNS-01

This directory contains the configuration to use [Traefik](https://traefik.io/) with the Mousetrap DNS-01 helper. It leverages Traefik's `exec` DNS provider to automate certificate issuance via Micetro.

## Components

- **Dockerfile**: Builds a custom Traefik image that includes `curl` and the DNS hook script.
- **dns-hook.sh**: A shell script that bridges Traefik (via Lego) and the Mousetrap API.
- **docker-compose.yaml**: A complete stack including Traefik and a sample web application (Nginx) configured for automated TLS.
- **dot.env.sample**: Sample environment variables for configuration.

## How it Works

Traefik uses the `exec` provider for the ACME DNS-01 challenge. When a certificate is requested, Traefik calls `/usr/local/bin/dns-hook.sh` with specific arguments:
1. `present` or `cleanup`: The action to perform.
2. `fqdn`: The `_acme-challenge` domain.
3. `value`: The TXT record value.

The hook script then sends a REST request to the Mousetrap API to manage the records in Micetro.

## Configuration

1.  **Prepare Environment**:
    ```bash
    cp dot.env.sample .env
    ```

2.  **Edit `.env`**:
    - `MICETRO_API_URL`: URL of your Mousetrap service.
    - `MICETRO_TOKEN`: Your authentication token.
    - `MICETRO_ZONE`: The target DNS zone.
    - `APP_DOMAIN`: The domain for your web application.
    - `TRAEFIK_PROFILE`: A unique identifier for this Traefik instance (useful when running multiple instances on one host).

## Profiles and Constraints

The `docker-compose.yaml` is configured with **Docker Constraints**. Traefik will only manage containers that have a matching `traefik.profile` label. This allows you to run multiple independent Traefik instances on the same Docker host without them interfering with each other's containers.

## Usage

Start the stack:
```bash
docker compose up -d
```

Traefik will start, detect the `webapp` container, and automatically request a Let's Encrypt certificate using the DNS-01 challenge through Mousetrap.

## Notes

- **Staging**: By default, the configuration uses the Let's Encrypt **Staging** server. Remove the `--certificatesresolvers.myresolver.acme.caserver` line in `docker-compose.yaml` for production certificates.
- **Persistence**: Certificates are stored in `./letsencrypt/acme.json`. Ensure this file has appropriate permissions (`600`).
