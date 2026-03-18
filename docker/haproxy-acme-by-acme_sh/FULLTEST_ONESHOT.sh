#!/usr/bin/env sh

set -eu

rm -rf certs
mkdir -p certs

./START_ACME_ONESHOT.sh
