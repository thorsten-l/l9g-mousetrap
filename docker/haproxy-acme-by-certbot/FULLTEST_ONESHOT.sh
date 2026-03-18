#!/usr/bin/env sh

set -eu

rm -rf certbot-data certs
mkdir -p certbot-data certs

./START_CERTBOT_ONESHOT.sh
