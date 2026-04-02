#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose up -d --build
echo "Stack na raiz do repo. api-1: http://localhost:8080  |  api-2 (dev UI): http://localhost:8081/q/dev-ui  |  RabbitMQ: http://localhost:15672"
