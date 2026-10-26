#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
CONTAINER_NAME=prototipo-ventas-postgres
VOLUME_NAME=prototipo-ventas-postgres-data
ENV_FILE=${ENV_FILE:-.env}

cd "$ROOT_DIR"
case "$ENV_FILE" in
    /*) ;;
    *) ENV_FILE="$ROOT_DIR/$ENV_FILE" ;;
esac

if [ ! -f "$ENV_FILE" ]; then
    printf '%s\n' 'Falta .env. Copia .env.example a .env y configura una contraseña local.' >&2
    exit 1
fi

set -a
. "$ENV_FILE"
set +a

: "${POSTGRES_DB:?Define POSTGRES_DB en .env}"
: "${POSTGRES_USER:?Define POSTGRES_USER en .env}"
: "${POSTGRES_PASSWORD:?Define POSTGRES_PASSWORD en .env}"
POSTGRES_PORT=${POSTGRES_PORT:-55432}

remove_container() {
    docker rm --force "$CONTAINER_NAME" >/dev/null 2>&1 || true
}

start_database() {
    if docker container inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
        state=$(docker inspect --format '{{.State.Status}}' "$CONTAINER_NAME")
        if [ "$state" != running ]; then
            docker start "$CONTAINER_NAME" >/dev/null
        fi
    else
        docker volume create "$VOLUME_NAME" >/dev/null
        docker run --detach \
            --name "$CONTAINER_NAME" \
            --restart unless-stopped \
            --publish "$POSTGRES_PORT:5432" \
            --env "POSTGRES_DB=$POSTGRES_DB" \
            --env "POSTGRES_USER=$POSTGRES_USER" \
            --env "POSTGRES_PASSWORD=$POSTGRES_PASSWORD" \
            --mount "type=volume,source=$VOLUME_NAME,target=/var/lib/postgresql/data" \
            --mount "type=bind,source=$ROOT_DIR/src/main/resources/db/schema.sql,target=/docker-entrypoint-initdb.d/01-schema.sql,readonly" \
            --mount "type=bind,source=$ROOT_DIR/src/main/resources/db/dev-seed.sql,target=/docker-entrypoint-initdb.d/02-dev-seed.sql,readonly" \
            --health-cmd 'pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"' \
            --health-interval 2s \
            --health-timeout 3s \
            --health-retries 30 \
            postgres:16.15 >/dev/null
    fi

    attempts=0
    while [ "$attempts" -lt 45 ]; do
        health=$(docker inspect --format '{{.State.Health.Status}}' "$CONTAINER_NAME")
        if [ "$health" = healthy ]; then
            printf 'PostgreSQL listo en localhost:%s (%s)\n' "$POSTGRES_PORT" "$POSTGRES_DB"
            return
        fi
        if [ "$health" = unhealthy ]; then
            docker logs "$CONTAINER_NAME" >&2
            return 1
        fi
        attempts=$((attempts + 1))
        sleep 1
    done

    docker logs "$CONTAINER_NAME" >&2
    printf '%s\n' 'PostgreSQL no quedó listo dentro del tiempo esperado.' >&2
    return 1
}

case "${1:-}" in
    up)
        start_database
        ;;
    down)
        remove_container
        printf '%s\n' 'Contenedor detenido; los datos del volumen se conservaron.'
        ;;
    reset)
        remove_container
        docker volume rm "$VOLUME_NAME" >/dev/null 2>&1 || true
        start_database
        ;;
    *)
        printf 'Uso: sh scripts/postgres.sh {up|down|reset}\n' >&2
        exit 2
        ;;
esac