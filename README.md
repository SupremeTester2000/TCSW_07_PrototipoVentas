# Prototipo Ventas

Aplicación de gestión de productos y ventas con Java 11, Spring Boot 2.7.18, Swing como cliente visual legado, arquitectura hexagonal, Hibernate ORM y PostgreSQL 16.15. El proyecto mantiene la separación entre dominio, casos de uso, puertos y adaptadores, y añade Spring Boot como infraestructura externa de ejecución, configuración e inyección de dependencias.

## Arquitectura y capa de persistencia

`Product` representa el catálogo del negocio; `Sale` calcula subtotal, descuento, IVA y total; `SaleDetail` conserva el precio capturado al momento de la venta. Las políticas de descuento y los cálculos de impuestos se resuelven mediante estrategias.

La composición de dependencias se realiza en `com.example.config.SpringConfiguration`, y la entrada principal de Spring Boot es `com.example.SalesApplication`. La capa de dominio y los puertos no dependen ni de Spring ni de Hibernate ni de JDBC.

Los adaptadores Hibernate están en `com.example.adapters.outbound.persistence`. El esquema DDL y los registros de demostración se cargan desde `src/main/resources/db/schema.sql` y
`src/main/resources/db/dev-seed.sql`. La confirmación del stock y la persistencia de la venta se resuelven en una misma transacción para mantener la consistencia del negocio.

## API REST

El adaptador REST expone los siguientes recursos. Sus DTOs son propios de `com.example.adapters.inbound.rest`; no expone entidades Hibernate ni modelos del dominio como JSON y delega las operaciones en `SaleFacadePort`.

| Método y ruta | Descripción |
|---|---|
| `GET /api/products` | Lista productos como `ProductResponse`. |
| `POST /api/products` | Registra un producto y devuelve `201 Created`. El cuerpo contiene `name`, `price` y `stock`; el código lo genera la aplicación. |
| `PUT /api/products/{code}` | Actualiza el producto identificado por la ruta. El cuerpo contiene `name`, `price` y `stock`; no se acepta `code` en el cuerpo. |
| `POST /api/sales` | Procesa una venta. Conserva el contrato de entrada como objeto JSON cuyas claves son códigos y cuyos valores son cantidades. |

Ejemplo de alta de producto:

```json
{
  "name": "Café",
  "price": 12.50,
  "stock": 4
}
```

Ejemplo de solicitud de venta:

```json
{
  "P00001": 2
}
```

Los campos requeridos, formatos numéricos, códigos y cantidades se validan en la entrada HTTP. Las reglas de negocio, como la disponibilidad de stock, permanecen en el dominio y los casos de uso. Las solicitudes con estructura o formato inválidos responden con `400 Bad Request`.

## Requisitos y reproducibilidad

Requisitos del entorno:

- JDK 11
- Docker CLI con el daemon activo
- Docker Compose
- Maven 3.x o el wrapper incluido (`mvnw`/`mvnw.cmd`)

El proyecto usa PostgreSQL 16.15 con variables de entorno definidas en `.env` y un volumen persistente nombrado `prototipo-ventas-postgres-data`.

### 1. Preparación del entorno local

```bash
cp .env.example .env
# Edita la contraseña local antes de iniciar PostgreSQL.
```

El archivo `.env.example` define:

```bash
POSTGRES_DB=prototipo_ventas
POSTGRES_USER=ventas_local
POSTGRES_PASSWORD=replace-with-a-local-password
POSTGRES_PORT=55432
```

Importante: Docker Compose lee `.env` automáticamente, pero Java/Spring Boot no lo hace por sí mismo. Para ejecutar `./mvnw spring-boot:run` con la misma configuración del contenedor, la terminal debe exportar las variables antes del arranque.

### 2. Iniciar PostgreSQL

Se puede usar Docker Compose o el script de soporte incluido:

```bash
# Opción recomendada con Compose
cp .env.example .env
docker compose up -d
docker compose ps
```

O bien:

```bash
sh scripts/postgres.sh up
```

`compose.yaml` expone PostgreSQL en `localhost:${POSTGRES_PORT}` y monta el esquema y los datos de prueba desde `src/main/resources/db/` en el directorio de inicialización del contenedor.

### 3. Exportar variables para la aplicación

La base de datos se define mediante variables `POSTGRES_*` y la capa de infraestructura construye la URL JDBC a partir de ellas. Esta estrategia se usa también para mantener la prueba de integración y la ejecución de Spring Boot con una sola configuración coherente.

```bash
set -a
. ./.env
set +a
export POSTGRES_HOST=localhost
export POSTGRES_PORT=${POSTGRES_PORT}
export POSTGRES_DB=${POSTGRES_DB}
export POSTGRES_USER=${POSTGRES_USER}
export POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
```

La URL final queda construida como:

```text
jdbc:postgresql://localhost:55432/prototipo_ventas
```

Esto permite reproducir exactamente el mismo entorno de conexión entre el arranque del contenedor, Hibernate y la ejecución de Spring Boot.

### 4. Arranque de la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación usa la configuración de PostgreSQL cargada desde el entorno y crea un único `HibernateSessionFactory` que se inyecta en `HibernateSessionManager` para finalizar en los repositorios Hibernate.

### 5. Verificación del funcionamiento

Asegúrate de que el contenedor está en estado saludable:

```bash
docker compose ps
```

Y valora la conexión HTTP con la aplicación en funcionamiento:

```bash
curl http://localhost:8080/api/products
```

La respuesta esperada es un JSON con el catálogo cargado desde PostgreSQL.

## Ejecución

### Aplicación Spring Boot

```bash
set -a
. ./.env
set +a
./mvnw spring-boot:run
```

La aplicación principal es `com.example.SalesApplication` y arranca el contexto Spring Boot con `@SpringBootApplication`.

### Interfaz Swing (mantenimiento de la versión no web)

```bash
./mvnw exec:java -Dexec.mainClass=com.Main
```

La clase `com.Main` se conserva para compatibilidad con la composición manual original de Swing.

### Validación y pruebas

Suite completa del proyecto:

```bash
./mvnw clean verify
```

Prueba de integración con PostgreSQL habilitada:

```bash
RUN_POSTGRES_INTEGRATION=true ./mvnw -Dtest=HibernatePersistenceIntegrationTest test
```

La prueba de integración requiere que las variables de PostgreSQL estén exportadas en la misma terminal antes de lanzarla. La estrategia actual usa `POSTGRES_*` de forma consistente con Docker Compose y con la configuración Hibernate del proyecto.

## Verificación de módulo y recuperación del entorno

Para validar la base de datos y la suite de integración con PostgreSQL:

```bash
sh scripts/verify-module.sh M11
```

El script comprueba el estado de Compose, inicia PostgreSQL si es necesario y ejecuta el proceso de validación completo sin destruir el volumen persistente.

Si se requiere reconstruir la base desde cero, se puede usar el reset controlado:

```bash
CONFIRM_POSTGRES_RESET=prototipo-ventas-postgres-data sh scripts/postgres.sh reset
```

`docker compose down` detiene y elimina el contenedor, pero conserva el volumen de datos. Para reaplicar el esquema y los datos de demostración desde cero, se debe usar el reset anterior con confirmación explícita.

## Documentación adicional

- [Propuesta del modelo relacional y plan Hibernate](docs/propuesta-persistencia-hibernate.md)
- [Análisis de patrones de ventas](docs/analisis-patrones-ventas.md)
- [Estilografía del proyecto](docs/estilografia.md).
