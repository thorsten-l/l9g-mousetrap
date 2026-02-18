# Load variables
source .env

# --server letsencrypt_test is the alias for the staging URL
docker exec acme acme.sh --issue \
  --server letsencrypt_test \
  -d "$APP_DOMAIN" \
  --dns dns_mousetrap \
  --debug 2
