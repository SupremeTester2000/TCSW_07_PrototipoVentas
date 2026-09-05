# Sistema de gestión de Ventas - Tecnologías para la construcción de software

**Java JDK:** 11
**Apache Maven:** 3.9.16

## Estructura del Dominio
**Producto**: Representa un artículo a añadirse al sistema, se implementó un encampsulamiento para que su código y nombre no puedan estar vacíos.

**DetalleVenta**: Modela la línea de venta enlazada a un producto, asegurando que la cantidad solicitada no supere al stock disponible.

**Venta**: Gestiona el conjunto de detalles de la transacción y calcula el monto total acumulado.

**'Venta'**: Gestiona el conjunto de detalles de la transacción y calcula el monto total acumulado.

## Ejecución

Desde la raíz del proyecto:

```bash
mvn clean test
```

El proyecto fue comprobado con 9 pruebas automatizadas, todas obtuvieron un resultado exitoso.

## Calidad

Se sometió a una revisión de código con la herramienta SonarQube para el IDE Visual Studio Code.