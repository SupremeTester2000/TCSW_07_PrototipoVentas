# Estrategias de descuento y configuración global

## Cálculo de venta

`Sale` conserva el subtotal y delega el descuento a una función `Function<Sale, BigDecimal>` y el impuesto a `TaxStrategy`. No selecciona políticas con `if/else` ni `switch`. El total se calcula como:

```text
base gravable = subtotal - descuento
IVA = redondear(base gravable * tasa IVA, 2 decimales)
total = base gravable + IVA
```

Los porcentajes de descuento se expresan en puntos porcentuales (por ejemplo, `10` significa 10 %). Todos los importes calculados se redondean a dos decimales con `HALF_UP`. Los descuentos se limitan al subtotal y al máximo configurado para evitar bases gravables negativas.

## Strategy clásico frente a funciones

| Criterio | Strategy clásico | Lambdas / `Function<Sale, BigDecimal>` |
| --- | --- | --- |
| Legibilidad | Cada política tiene nombre y tipo propio; la intención es clara en proyectos con varias reglas. | La política queda cerca del punto de configuración; breve para reglas pequeñas y directas. |
| Archivos Java adicionales para descuentos | 3: interfaz y dos implementaciones (`DiscountStrategy`, `DiscountPercent`, `FixedDiscount`). | 1: `FunctionalDiscountPolicies`, con fábricas que retornan funciones. |
| Mantenibilidad | Facilita añadir estado, validación específica, documentación y pruebas por política. | Reduce archivos y ceremonia, pero una regla extensa puede volverse difícil de leer y versionar. |

La capa de dominio soporta ambos enfoques: las clases clásicas implementan `Function`, y el servicio acepta una función de descuento. `Main` muestra la configuración funcional; se puede pasar una instancia de `DiscountPercent` o `FixedDiscount` para usar Strategy clásico sin cambiar `Sale` ni el servicio.

## Singleton de configuración

`ConfiguracionVentasSingleton` es adecuado aquí como punto único de verdad para parámetros compartidos por el módulo: tasa de IVA predeterminada, moneda base, máximo de detalles por venta y descuento máximo. Las estrategias consultan esa configuración al calcular descuentos o impuestos, y `Sale` aplica el límite de detalles.

La inicialización diferida usa el idiom Holder de Java: la clase interna se inicializa al primer acceso, y el mecanismo de inicialización de clases de la JVM garantiza publicación segura y una única instancia. Los parámetros mutables se guardan en campos `volatile`; cada lectura o escritura individual es visible entre hilos. Las actualizaciones de propiedades distintas no forman una transacción atómica.

### Riesgos y límites

- El acceso estático introduce acoplamiento y dependencias ocultas frente a pasar una configuración explícita por constructor.
- El estado global mutable puede filtrarse entre pruebas y hacer que su resultado dependa del orden; las pruebas deben restaurar valores o sustituir el singleton por una abstracción inyectable.
- Un Singleton concreto es incómodo de mockear, porque el consumidor obtiene una instancia global y no una dependencia reemplazable.
- `volatile` asegura visibilidad de cada propiedad, pero no una instantánea coherente de varias propiedades actualizadas a la vez.
- En despliegues multi-tenant, procesos distribuidos o configuración por solicitud, una instancia global en la JVM no representa una fuente de verdad suficiente.

En esta aplicación pequeña, centralizar valores predeterminados compartidos hace explícita su unicidad. Si aparecen perfiles, tenants o cambios dinámicos coordinados, conviene reemplazar el acceso directo al Singleton por un puerto de configuración inyectado y mantener la configuración inmutable por operación.