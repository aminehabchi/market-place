#!/bin/bash

# Stop all running containers
docker compose -f ../gateway/docker-compose.yaml down -v
docker compose -f ../products-servise/docker-compose.yaml down -v
docker compose -f ../kafka/docker-compose.yaml down -v
docker compose -f ../eureka-server/docker-compose.yaml down -v

# Remove the shared network
docker network rm shared-net