# Sistema de gestión de Ventas - Tecnologías para la construcción de software

**Java JDK:** 11
**Apache Maven:** 3.9.16

## Estructura del Dominio
**Product** representa un artículo; **SaleDetail** captura el precio histórico y cantidad; **Sale** calcula subtotal, descuento, IVA y total final delegando las políticas a estrategias.

Las políticas clásicas de descuento (`DiscountPercent` y `FixedDiscount`) implementan `DiscountStrategy`. También se ofrece la alternativa funcional `FunctionalDiscountPolicies`, que produce `Function<Sale, BigDecimal>`. La tasa de IVA, moneda base, límite de detalles y descuento máximo se administran mediante `ConfiguracionVentasSingleton`.

## Ejecución

Ejecuta la suite desde la raíz del proyecto:
```bash
mvn clean test
```

La comparación de enfoques y el análisis crítico del Singleton están en [docs/analisis-patrones-ventas.md](docs/analisis-patrones-ventas.md).

## Calidad

Se sometió a una revisión de código con la herramienta SonarQube para el IDE Visual Studio Code.

---
# Diagramas de clases del proyecto - Implementación de arquitectura hexagonal
<img width="1600" height="1119" alt="image" src="https://github.com/user-attachments/assets/4ee72933-7313-4fce-aa67-d2d89fca0e6d" />
