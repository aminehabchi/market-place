#!/bin/bash

docker compose -f ../gateway/docker-compose.yaml down
docker compose -f ../products-servise/docker-compose.yaml down
docker compose -f ../kafka/docker-compose.yaml down
docker compose -f ../eureka-server/docker-compose.yaml down
