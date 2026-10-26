# Propuesta de modelo relacional y plan Hibernate

## Estado actual del proyecto

- El proyecto usa Java 11 y Maven; `pom.xml` declara Hibernate ORM 6.6.18.Final y PostgreSQL JDBC 42.7.7.
- `ProductRepositoryPort` ofrece `save`, `findByCode` y `findAll`; `SaleRepositoryPort` ofrece `save`.
- Hay adaptadores en memoria para pruebas y adaptadores Hibernate para ejecución; `Main` configura los últimos mediante variables de entorno.
- El dominio contiene `Product`, `Sale` y `SaleDetail`. `SaleDetail` es inmutable, no tiene identidad propia y captura el precio de producto al construirlo. `Sale` deriva sus importes mediante estrategias y no tiene identificador ni fecha.
- El esquema versionado está en `src/main/resources/db/schema.sql`; Hibernate valida el esquema al iniciar.
- `ProcessSaleService` guarda la venta mediante `SaleRepositoryPort`; la actualización de existencias ocurre después mediante `StockUpdater` y `ProductRepositoryPort`.

## Modelo propuesto

El esquema conserva las tres entidades del diagrama. La entidad `sale_details` usa clave primaria compuesta porque el número de línea identifica un detalle dentro de una venta; además, la relación con `sales` es identificante. La referencia a `products` es una FK no identificante.

```mermaid
erDiagram
    PRODUCTS ||--o{ SALE_DETAILS : contiene
    SALES ||--|{ SALE_DETAILS : incluye

    PRODUCTS {
        varchar(6) code PK
        varchar(120) name
        numeric(10,2) price
        integer stock
    }
    SALES {
        bigint sale_id PK
        timestamp created_at
        numeric(12,2) subtotal
        numeric(12,2) discount_amount
        numeric(12,2) tax_amount
        numeric(12,2) total
    }
    SALE_DETAILS {
        bigint sale_id PK,FK
        smallint line_number PK
        varchar(6) product_code FK
        integer quantity
        numeric(10,2) captured_price
    }
```

### DDL para PostgreSQL 16

```sql
CREATE TABLE products (
    code          VARCHAR(6) PRIMARY KEY,
    name          VARCHAR(120) NOT NULL,
    price         NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock         INTEGER NOT NULL CHECK (stock >= 0)
);

CREATE TABLE sales (
    sale_id         BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal        NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0),
    discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (discount_amount >= 0),
    tax_amount      NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (tax_amount >= 0),
    total           NUMERIC(12, 2) NOT NULL CHECK (total >= 0)
);

CREATE TABLE sale_details (
    sale_id       BIGINT NOT NULL,
    line_number   SMALLINT NOT NULL,
    product_code  VARCHAR(6) NOT NULL,
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    captured_price NUMERIC(10, 2) NOT NULL CHECK (captured_price >= 0),
    CONSTRAINT pk_sale_details PRIMARY KEY (sale_id, line_number),
    CONSTRAINT fk_sale_details_sale FOREIGN KEY (sale_id)
        REFERENCES sales (sale_id),
    CONSTRAINT fk_sale_details_product FOREIGN KEY (product_code)
        REFERENCES products (code)
);

CREATE INDEX idx_sale_details_product_code ON sale_details (product_code);
```

El esquema respeta los tipos y restricciones del diagrama. `captured_price` preserva el precio histórico aunque cambie `products.price`; los importes de `sales` son una instantánea de la venta confirmada. `line_number` se asigna desde 1 en el orden de los detalles y el límite actual del dominio (100 detalles) cabe en `SMALLINT`.

## Diseño del adaptador Hibernate

- Crear entidades de persistencia exclusivamente bajo `com.example.adapters.outbound.persistence.entity` (por ejemplo, `ProductEntity`, `SaleEntity` y `SaleDetailEntity`). No agregar anotaciones Hibernate/JPA a las clases del dominio.
- Usar `@EmbeddedId`/`@Embeddable` o una estrategia equivalente para `(sale_id, line_number)`. Mapear las referencias mediante FKs; guardar `product_code` y `captured_price` como datos históricos del detalle.
- Crear mapeadores dentro del adaptador. El modelo persistente de venta debe recibir valores calculados al guardar (`subtotal`, descuento, impuesto y total) y la fecha generada por PostgreSQL; no introducir campos de infraestructura en `Sale`.
- Implementar `ProductRepositoryPort` y `SaleRepositoryPort` en adaptadores Hibernate. Mantener los adaptadores en el composition root de `Main` detrás de las interfaces existentes.
- Usar una `SessionFactory` de vida de aplicación y una `Session`/transacción por operación. En cada escritura: iniciar transacción, hacer `commit` solo si termina correctamente; ante excepción, hacer `rollback`, cerrar sesión y traducir el error a una excepción neutral de persistencia. `HibernateSessionManager` comparte la sesión dentro de una operación transaccional anidada.
- Configurar URL, usuario y contraseña mediante variables de entorno (`DB_URL`, `DB_USER`, `DB_PASSWORD`), sin credenciales en código o archivos versionados. Usar `hibernate.hbm2ddl.auto=validate`; el DDL del esquema se administra por separado.
- Mantener las consultas explícitas parametrizadas, por ejemplo HQL `from ProductEntity p where p.code = :code` con `setParameter("code", code)`. No concatenar valores externos ni permitir que Hibernate imprima parámetros sensibles en logs.
- Hibernate ORM 6.6.18.Final y PostgreSQL JDBC 42.7.7 están declarados en Maven; la configuración requiere `DB_URL`, `DB_USER` y `DB_PASSWORD`.
- Hibernate usa su pool JDBC interno, que emite una advertencia indicando que no está pensado para producción. Para producción debe sustituirse por un pool gestionado como HikariCP.

## Plan de implementación

1. **Hecho: fijar el esquema**. `schema.sql` crea tablas, claves, restricciones e índice; se aplicó y validó contra PostgreSQL 16.15.
2. **Hecho: añadir dependencias y configuración**. `HibernateSessionFactory` toma credenciales del entorno, configura el driver y valida el esquema al iniciar; `Main` cierra la factoría durante el apagado.
3. **Hecho: implementar entidades y mapeos**. Las entidades JPA están bajo el adaptador outbound y el detalle usa ID compuesto; las ventas guardan importes calculados y precio capturado.
4. **Hecho: implementar los puertos**. Los adaptadores cubren los métodos de producto y venta; las consultas escritas usan parámetros y las escrituras administran transacciones.
5. **Hecho: conectar el composition root**. `Main` usa Hibernate; los adaptadores en memoria se conservan para pruebas.
6. **Hecho: validar capas y comportamiento**. ArchUnit prohíbe Hibernate/JDBC en capas internas e inbound; una prueba opt-in comprueba persistencia, actualización de stock y rollback en PostgreSQL 16.

## Frontera transaccional implementada

`SaleFacade` ejecuta el caso de uso y la publicación del evento dentro de `TransactionPort`. El adaptador Hibernate enlaza las llamadas de los dos repositorios a la misma sesión y transacción en ese hilo. Si el guardado de venta o cualquier observer falla, se revierten tanto la venta como los cambios de stock. No se pasan `Session`, `SessionFactory`, JDBC ni SQL a servicios, dominio o UI. Los constructores existentes de `SaleFacade` se mantienen para compatibilidad; la configuración de producción inyecta explícitamente el adaptador transaccional.

## Criterios de aceptación verificables

- Maven resuelve Hibernate y PostgreSQL JDBC; la aplicación conecta a PostgreSQL 16 con configuración externa.
- El esquema se valida al inicio; producto, venta y detalles se persisten respetando claves, restricciones y precio histórico.
- Cada operación de escritura usa transacción y hace rollback ante error; los errores de Hibernate/JDBC se traducen antes de salir del adaptador.
- Las consultas usan parámetros; ninguna capa de dominio/aplicación/puertos depende de Hibernate, JDBC o PostgreSQL.
- `mvn clean test` continúa pasando; `HibernatePersistenceIntegrationTest` comprueba persistencia, precio histórico y rollback con PostgreSQL cuando se habilita explícitamente mediante `RUN_POSTGRES_INTEGRATION=true`.

## Ejecución local de PostgreSQL

La opción recomendada para iniciar la base es Docker Compose. Copia `.env.example` como `.env`, configura
una contraseña local y ejecuta:

```bash
docker compose up -d
docker compose ps
```

Compose monta `src/main/resources/db/schema.sql` y `dev-seed.sql` en el directorio de inicialización de
PostgreSQL. Esos scripts se ejecutan cuando se crea el volumen por primera vez. El volumen nombrado se
comparte con `scripts/postgres.sh`, que ofrece `up`, `down` y `reset` como alternativa de uso.

Para detener el servicio sin perder datos:

```bash
docker compose down
```

Para reinicializar el esquema y eliminar los datos persistidos:

```bash
docker compose down -v
docker compose up -d
```