# Prototipo Ventas

Aplicación de escritorio para gestionar productos y ventas. Usa Java 11, Swing, arquitectura
hexagonal, Hibernate ORM y PostgreSQL 16.15.

## Dominio y persistencia

`Product` representa un producto; `Sale` calcula subtotal, descuento, IVA y total; `SaleDetail` conserva
el precio capturado al momento de la venta. Las políticas de descuento e impuestos se aplican mediante
estrategias.

Los adaptadores Hibernate están en `com.example.adapters.outbound.persistence`; dominio, casos de uso
y puertos no dependen de Hibernate ni JDBC. El esquema y los datos de demostración están en
`src/main/resources/db/`. La persistencia y el stock de una venta se confirman en una sola transacción.

## Ejecución

Requisitos: JDK 11 y Docker CLI con el daemon iniciado. Maven 3.9.16 se descarga automáticamente con
el wrapper incluido. Prepara la base local; el schema y el producto de ejemplo se cargan al crear el
volumen por primera vez. `.env` está ignorado por Git; cambia la contraseña de ejemplo antes de usarla.

```bash
cp .env.example .env
# Edita POSTGRES_PASSWORD en .env antes de iniciar.
sh scripts/postgres.sh up

# Exporta los valores para que Hibernate se conecte desde esta terminal.
set -a
. ./.env
set +a
export DB_URL="jdbc:postgresql://localhost:${POSTGRES_PORT}/${POSTGRES_DB}"
export DB_USER="$POSTGRES_USER"
export DB_PASSWORD="$POSTGRES_PASSWORD"
```

Inicia la interfaz o ejecuta la suite completa:

```bash
./mvnw exec:java -Dexec.mainClass=com.Main
./mvnw clean verify
```

La suite normal no requiere PostgreSQL. Para ejecutar además la prueba de integración, mantén
exportadas las variables `DB_URL`, `DB_USER` y `DB_PASSWORD` en la misma terminal:

```bash
RUN_POSTGRES_INTEGRATION=true ./mvnw -Dtest=HibernatePersistenceIntegrationTest test
```

`sh scripts/postgres.sh down` elimina el contenedor y conserva los datos. `sh scripts/postgres.sh up`
lo vuelve a iniciar. Para recrear la base desde cero, ejecuta `sh scripts/postgres.sh reset`; **esto
elimina el volumen y todos sus datos**, y vuelve a aplicar schema y datos de demostración.

## Documentación

- [Propuesta del modelo relacional y plan Hibernate](docs/propuesta-persistencia-hibernate.md)
- [Análisis de patrones de ventas](docs/analisis-patrones-ventas.md)
- [Estilografía del proyecto](docs/estilografia.md).
