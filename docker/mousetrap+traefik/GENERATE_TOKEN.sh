#!/bin/bash

docker run --rm \
  -v ./data:/data:ro \
  -v ./l9g-mousetrap.jar:/l9g-mousetrap.jar:ro \
   bellsoft/liberica-openjdk-alpine:21 \
   java -jar /l9g-mousetrap.jar -g
