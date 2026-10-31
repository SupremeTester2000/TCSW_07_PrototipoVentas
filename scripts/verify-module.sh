#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT_DIR"

if [ "$#" -ne 1 ] || [ "$1" != M11 ]; then
    printf 'Uso: sh scripts/verify-module.sh M11\n' >&2
    exit 2
fi

if [ ! -f .env ]; then
    printf '%s\n' 'Falta .env. Copia .env.example a .env y configura credenciales locales.' >&2
    exit 1
fi

set -a
. ./.env
set +a

: "${POSTGRES_DB:?Define POSTGRES_DB en .env}"
: "${POSTGRES_USER:?Define POSTGRES_USER en .env}"
: "${POSTGRES_PASSWORD:?Define POSTGRES_PASSWORD en .env}"
POSTGRES_PORT=${POSTGRES_PORT:-55432}

if ! docker compose version >/dev/null 2>&1; then
    printf '%s\n' 'Falta el complemento Docker Compose. Instálalo para validar compose.yaml y ejecutar M11.' >&2
    exit 1
fi

docker compose config -q

if docker container inspect prototipo-ventas-postgres >/dev/null 2>&1; then
    printf '%s\n' 'Se reutilizará el contenedor PostgreSQL existente.'
else
    docker compose up -d postgres
fi

attempts=0
while [ "$attempts" -lt 60 ]; do
    health=$(docker inspect --format '{{.State.Health.Status}}' prototipo-ventas-postgres 2>/dev/null || true)
    if [ "$health" = healthy ]; then
        break
    fi
    if [ "$health" = unhealthy ]; then
        docker compose logs --no-color postgres >&2
        printf '%s\n' 'PostgreSQL no pasó su comprobación de salud.' >&2
        exit 1
    fi
    attempts=$((attempts + 1))
    sleep 1
done

if [ "$health" != healthy ]; then
    docker compose logs --no-color postgres >&2
    printf '%s\n' 'PostgreSQL no quedó listo dentro del tiempo esperado.' >&2
    exit 1
fi

export DB_URL="jdbc:postgresql://localhost:${POSTGRES_PORT}/${POSTGRES_DB}"
export DB_USER="$POSTGRES_USER"
export DB_PASSWORD="$POSTGRES_PASSWORD"
export RUN_POSTGRES_INTEGRATION=true

./mvnw clean verify

printf '%s\n' 'MODULO_M11_VERIFICADO'
