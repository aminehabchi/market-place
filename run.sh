#!/bin/bash

docker compose -f ./eureka-server/docker-compose.yaml up -d

docker compose -f ./kafka/docker-compose.yaml up -d

docker compose -f ./products-servise/docker-compose.yaml up -d

docker compose -f ./gateway/docker-compose.yaml up -d