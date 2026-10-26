#!/usr/bin/env bash
set -euo pipefail

ENV_FILE=".env"
if [[ -f "$ENV_FILE" ]]; then
  set -a
  source "$ENV_FILE"
  set +a
fi

POSTGRES_DB="${POSTGRES_DB:-prototipo_ventas}"
POSTGRES_USER="${POSTGRES_USER:-ventas_local}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-replace-with-a-local-password}"
POSTGRES_PORT="${POSTGRES_PORT:-55432}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

docker run --rm \
  --name prototipo_ventas_postgres \
  -e POSTGRES_DB="$POSTGRES_DB" \
  -e POSTGRES_USER="$POSTGRES_USER" \
  -e POSTGRES_PASSWORD="$POSTGRES_PASSWORD" \
  -p "${POSTGRES_PORT}:5432" \
  -v postgres_data:/var/lib/postgresql/data \
  -v "$PROJECT_ROOT/src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql:ro" \
  -v "$PROJECT_ROOT/src/main/resources/db/dev-seed.sql:/docker-entrypoint-initdb.d/02-dev-seed.sql:ro" \
  postgres:16.15
