# Informe del incremento: infraestructura PostgreSQL

## 1. Objetivo

Este incremento incorpora la infraestructura local necesaria para ejecutar el sistema de ventas con PostgreSQL 16. La base de datos se administra mediante Docker Compose y obtiene su estructura a partir de un único archivo SQL versionado en el proyecto.

## 2. Alcance

El trabajo se limitó a la infraestructura y su documentación. No se modificaron las entidades del dominio, los puertos, los casos de uso, los adaptadores Hibernate ni la interfaz Swing. La base de datos permanece como un detalle externo de persistencia.

## 3. Artefactos incorporados

- `compose.yaml`: define el servicio de PostgreSQL, sus variables de entorno, puerto publicado, volumen persistente, scripts de inicialización y healthcheck.
- `src/main/resources/db/schema.sql`: define las tablas, claves y restricciones del modelo relacional.
- `src/main/resources/db/dev-seed.sql`: incorpora productos de ejemplo para desarrollo.
- `.env.example`: proporciona los nombres y valores de ejemplo para configurar la base localmente sin versionar credenciales reales.
- `scripts/postgres.sh`: conserva una alternativa basada en `docker run`, alineada con la imagen, variables y archivos SQL del proyecto.
- `docs/propuesta-persistencia-hibernate.md`: documenta el modelo relacional y la ejecución local.
- `README.md`: agrega las instrucciones básicas para levantar, detener y recrear la base de datos.
- `.gitignore`: ignora `.env` y conserva versionable `.env.example`.

### Componentes identificados

| Componente | Responsabilidad técnica |
|---|---|
| `compose.yaml` | Define y coordina el servicio PostgreSQL, su configuración, persistencia, inicialización y healthcheck. |
| `schema.sql` | Declara las tablas `products`, `sales` y `sale_details`, así como sus claves y restricciones. |
| `dev-seed.sql` | Carga productos de ejemplo para facilitar el desarrollo local. |
| `.env.example` | Proporciona las variables de configuración necesarias sin incluir credenciales reales. |
| `postgres_data` | Volumen nombrado que conserva los datos entre reinicios y detenciones del servicio. |
| `scripts/postgres.sh` | Ofrece una alternativa de ejecución con `docker run` usando la misma imagen, variables y scripts SQL. |
| Healthcheck `pg_isready` | Comprueba periódicamente que PostgreSQL acepte conexiones para el usuario y la base configurados. |

## 4. Modelo relacional

El esquema se compone de las siguientes tablas:

| Tabla | Responsabilidad | Clave primaria |
|---|---|---|
| `products` | Catálogo de productos, precio y existencias | `code` |
| `sales` | Datos generales y totales de una venta | `sale_id` |
| `sale_details` | Líneas de productos incluidas en una venta | `(sale_id, line_number)` |

`sale_details` se relaciona con `sales` mediante una clave foránea a `sale_id` y con `products` mediante una clave foránea a `code`. De esta manera, cada venta puede tener varias líneas y un producto puede aparecer en varias líneas de venta.

La columna `captured_price` conserva el precio del producto al momento de la venta, independientemente de los cambios posteriores en `products.price`. El esquema incluye las restricciones `NOT NULL`, `CHECK`, claves primarias y claves foráneas definidas para el modelo aprobado.

## 5. Ejecución con Docker Compose

El servicio usa la imagen `postgres:16.15`, publica el puerto configurado por `POSTGRES_PORT` en el puerto 5432 del contenedor y lee `POSTGRES_DB`, `POSTGRES_USER` y `POSTGRES_PASSWORD` desde el entorno.

El volumen nombrado `postgres_data` conserva los datos. Compose monta `schema.sql` y `dev-seed.sql` como `01-schema.sql` y `02-dev-seed.sql` en el directorio de inicialización oficial de la imagen. PostgreSQL ejecuta dichos archivos al inicializar un volumen vacío; por eso, un `docker compose down` seguido de `docker compose up -d` mantiene los datos y no vuelve a crear el esquema.

El healthcheck consulta la disponibilidad de la base con `pg_isready`, usando el usuario y la base definidos en el entorno. La configuración establece un intervalo de 10 segundos, un timeout de 5 segundos, cinco reintentos y un periodo inicial de 10 segundos.

## 6. Pasos de uso

Preparar el archivo local de variables y ajustar la contraseña:

```bash
cp .env.example .env
```

Levantar el servicio y revisar su estado:

```bash
docker compose up -d
docker compose ps
```

Cuando el contenedor esté disponible, se pueden consultar las tablas desde `psql` dentro del contenedor:

```bash
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "\dt"'
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "\d products"'
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "\d sales"'
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "\d sale_details"'
```

Detener los servicios sin eliminar los datos:

```bash
docker compose down
```

Reinicializar desde cero, eliminando también el volumen y sus datos:

```bash
docker compose down -v
docker compose up -d
```

## 7. Validación y evidencia

Se ejecutó `docker compose config` y la configuración se resolvió correctamente usando las variables de `.env`. No fue posible iniciar el contenedor ni comprobar el estado `healthy` o consultar las tablas en tiempo de ejecución, porque el daemon de Docker no estaba disponible en el entorno de trabajo. Estas verificaciones deben completarse en un equipo con Docker activo.

El proyecto ya contaba con una suite de pruebas Java, ejecutada antes de los cambios de infraestructura; la configuración de PostgreSQL no modifica el código de aplicación.

### Capturas sugeridas

Las capturas pueden agregarse en los espacios siguientes. Se recomienda ocultar cualquier contraseña o dato sensible.

1. **Configuración de Compose:** mostrar `compose.yaml` con la imagen, variables, volumen y healthcheck.

   _Insertar captura aquí._

2. **Esquema relacional:** mostrar `schema.sql` o la salida de `\dt` y `\d` para las tres tablas.

   _Insertar captura aquí._

3. **Servicio disponible:** mostrar la salida de `docker compose ps` con el estado `healthy`.

   _Insertar captura aquí._

4. **Persistencia:** opcionalmente, mostrar que los datos continúan disponibles después de detener y volver a iniciar el servicio sin eliminar el volumen.

   _Insertar captura aquí._

## 8. Resultado

El proyecto cuenta con una configuración reproducible para levantar PostgreSQL 16 mediante Docker Compose, inicializar el esquema y cargar datos de desarrollo en un volumen persistente. El esquema aprobado permanece en una única fuente SQL, separado de los datos de ejemplo, y la arquitectura de la aplicación no se acopla directamente a PostgreSQL.
