# Mousetrap DNS-01 Challenge with acme.sh

This directory contains configuration and scripts to use [acme.sh](https://github.com/acmesh-official/acme.sh) with the Mousetrap DNS-01 helper for obtaining SSL/TLS certificates via Micetro.

## Overview

The setup utilizes a custom DNS hook (`dns_mousetrap.sh`) that communicates with the Mousetrap API to automate the creation and deletion of DNS TXT records required for the ACME DNS-01 challenge.

## Directory Structure

- `acme-dns-hooks/`: Contains the `dns_mousetrap.sh` script, which is mounted into the container.
- `certs/`: Persistent storage for issued certificates and account information.
- `docker-compose-daemon.yaml`: Configuration for running `acme.sh` as a long-running service.
- `docker-compose-oneshot.yaml`: Configuration for running `acme.sh` as a temporary task.
- `START_ACME_ONESHOT.sh`: Helper script to issue a certificate using a temporary container.
- `START_ACME_ON_DAEMON_CONTAINER.sh`: Helper script to issue a certificate using a running daemon container.

## Configuration

1.  **Prepare Environment Variables**:
    Copy the sample environment file and edit it with your credentials:
    ```bash
    cp dot.env.sample .env
    ```

2.  **Edit `.env`**:
    - `MICETRO_API_URL`: The URL of your Mousetrap service (e.g., `http://mousetrap:8080/api/v1/micetro`).
    - `MICETRO_TOKEN`: Your Mousetrap authentication token.
    - `MICETRO_ZONE`: The DNS zone where the challenge record should be created (e.g., `example.com`).
    - `APP_DOMAIN`: The domain name you are requesting a certificate for.

## Usage Modes

### 1. One-Shot Mode (Recommended for simple automation)

Runs a container, issues the certificate, and then removes the container.

- **Run command**: `./START_ACME_ONESHOT.sh`
- **Uses**: `docker-compose-oneshot.yaml`

### 2. Daemon Mode (Recommended for automatic renewals)

Runs `acme.sh` as a background service.

- **Start Daemon**:
  ```bash
  docker compose -f docker-compose-daemon.yaml up -d
  ```
- **Issue Certificate**: `./START_ACME_ON_DAEMON_CONTAINER.sh`
- **Uses**: `docker-compose-daemon.yaml`

## Custom DNS Hook Details

The hook script `acme-dns-hooks/dns_mousetrap.sh` implements the `dns_mousetrap_add` and `dns_mousetrap_rm` functions required by `acme.sh`. It automatically calculates the record name by stripping the zone from the full challenge domain and performs the API calls to Mousetrap.

## Manual Issuance Example

To manually trigger an issuance using a running `acme` container:

```bash
docker exec acme acme.sh --issue \
  --server letsencrypt \
  -d your-domain.com \
  --dns dns_mousetrap
```
