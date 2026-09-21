# Sistema de gestión de Ventas - Tecnologías para la construcción de software

**Java JDK:** 11
**Apache Maven:** 3.9.16

## Estructura del Dominio
**Producto**: Representa un artículo a añadirse al sistema, se implementó un encampsulamiento para que su código y nombre no puedan estar   vacíos.

**DetalleVenta**: Modela la línea de venta enlazada a un producto, asegurando que la cantidad solicitada no supere al stock disponible.

**Venta**: Gestiona el conjunto de detalles de la transacción y calcula el monto total acumulado.

## Ejecución

La suite de pruebas para el módulo de ventas asegura la integridad del dominio comprobando escenarios clave.

1) Cálculos Financieros Exactos.
2) Validación de Reglas de Cantidad.
3) Integridad y Consistencia del Estado.
4) Inmutabilidad de Precios Históricos.

Desde la raíz del proyecto ejecutar el siguiente comando:
```bash
mvn clean test
```

El proyecto fue comprobado con 9 pruebas automatizadas, todas obtuvieron un resultado exitoso.

## Calidad

Se sometió a una revisión de código con la herramienta SonarQube para el IDE Visual Studio Code.

---
# Diagramas de clases del proyecto - Implementación de arquitectura hexagonal
<img width="1600" height="1119" alt="image" src="https://github.com/user-attachments/assets/4ee72933-7313-4fce-aa67-d2d89fca0e6d" />
