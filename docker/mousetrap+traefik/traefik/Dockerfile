FROM traefik:v3.6
RUN apk add --no-cache curl
COPY dns-hook.sh /usr/local/bin/dns-hook.sh
RUN chmod +x /usr/local/bin/dns-hook.sh
