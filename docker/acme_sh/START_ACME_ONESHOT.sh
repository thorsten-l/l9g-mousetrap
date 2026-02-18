# Load variables
source .env

docker compose -f docker-compose-oneshot.yaml run --rm acme acme.sh --issue \
  -d "$APP_DOMAIN" \
  --server letsencrypt_test \
  --dns dns_mousetrap \
  --debug 2 --force

# deploy certificate
# docker compose -f docker-compose-oneshot.yaml run --rm acme acme.sh --deploy \
#  -d "$APP_DOMAIN" \
#  --deploy-hook docker \
#  --domain-reload-container "nginx" \
#  --domain-reload-cmd "nginx -s reload"
