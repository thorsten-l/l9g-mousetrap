# Samples

Dieses Verzeichnis enthält Client-seitige Beispiele und Integrations-Skripte für l9g-mousetrap.

> **Hinweis:** Die meisten vollständigen Deployment-Beispiele (Docker Compose, acme.sh, certbot, dehydrated, Traefik, HAProxy, nginx, Apache, Tomcat) befinden sich im Verzeichnis [`../docker/`](../docker/).

## Inhalt

| Verzeichnis  | Beschreibung                                                         |
|--------------|----------------------------------------------------------------------|
| `powershell/` | DNS-Hook und Win-ACME-Integration für Windows (PowerShell 5.1+) |

## Übersicht Docker-Beispiele

| Verzeichnis                          | Beschreibung                                      |
|--------------------------------------|---------------------------------------------------|
| `docker/mousetrap/`                  | Standalone l9g-mousetrap Service                  |
| `docker/mousetrap+traefik/`          | l9g-mousetrap + Traefik (integriertes ACME)       |
| `docker/traefik/`                    | Traefik Reverse Proxy + Mousetrap DNS-01          |
| `docker/acme_sh/`                    | acme.sh standalone (ohne Webserver)               |
| `docker/haproxy-acme-by-acme_sh/`   | HAProxy + acme.sh                                 |
| `docker/haproxy-acme-by-certbot/`   | HAProxy + certbot                                 |
| `docker/haproxy-acme-by-dehydrated/`| HAProxy + dehydrated                              |
| `docker/nginx-acme-by-acme_sh/`     | nginx Webserver + acme.sh                         |
| `docker/nginx-proxy-acme-by-acme_sh/`| nginx Reverse Proxy + acme.sh                   |
| `docker/apache-httpd-acme-by-acme_sh/`| Apache HTTPD Webserver + acme.sh               |
| `docker/apache-httpd-proxy-acme-by-acme_sh/`| Apache HTTPD Reverse Proxy + acme.sh    |
| `docker/tomcat-acme-by-acme_sh/`    | Apache Tomcat Webserver + acme.sh                 |
