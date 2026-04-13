#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${ROOT_DIR}"

compose_up() {
  local compose_file="$1"
  echo "[CD] Applying ${compose_file}"
  docker-compose -f "${compose_file}" up -d --build
}

if ! docker network inspect shared-net >/dev/null 2>&1; then
  echo "[CD] Creating shared Docker network: shared-net"
  docker network create shared-net
fi

compose_up "eureka-server/docker-compose.yaml"
compose_up "redis/docker-compose.yaml"
compose_up "kafka/docker-compose.yaml"
compose_up "products-service/docker-compose.yaml"
compose_up "media-service/docker-compose.yaml"
compose_up "users-service/docker-compose.yaml"
compose_up "gateway/docker-compose.yaml"

echo "[CD] Deployment completed successfully."
