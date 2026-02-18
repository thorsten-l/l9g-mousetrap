#!/bin/bash

docker run --rm \
  -v ../data:/data:ro \
  -it ghcr.io/thorsten-l/l9g-uidgen:latest -g
