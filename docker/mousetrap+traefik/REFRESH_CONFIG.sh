#!/bin/bash

docker compose exec l9g-mousetrap wget -qO- --header="Content-Type: application/json" --post-data="" http://localhost:9000/actuator/refresh

