# Propuesta de persistencia con Hibernate y PostgreSQL

Esta propuesta conserva la arquitectura hexagonal del proyecto y deja la capa de dominio sin dependencias de infraestructura.

## Modelo relacional

La base de datos se compone de tres tablas:

- `products`: catálogo de productos con código primario `code` y validaciones para `price` y `stock`.
- `sales`: cabecera de ventas con `sale_id` serial, timestamps y totales.
- `sale_details`: líneas pertenecientes a una venta; cada fila conserva `quantity` y `captured_price` para garantizar la historia de la venta.

## Cardinalidades

- `products` 1 ---- N `sale_details`
- `sales` 1 ---- N `sale_details`

`captured_price` se conserva porque refleja el precio del producto en el momento de la venta; no debe depender del valor actual en `products.price`.

## Infraestructura de desarrollo

La base se levanta con Docker Compose usando PostgreSQL 16. El arranque usa `compose.yaml` junto con las variables declaradas en `.env.example`. El archivo `schema.sql` es la fuente única de verdad para la estructura, y `dev-seed.sql` aporta datos de demostración seguros para desarrollo.

```bash
cp .env.example .env
# editar la contraseña local

docker compose up -d

docker compose ps
```

Para recrear la base desde cero:

```bash
docker compose down -v
docker compose up -d
```
