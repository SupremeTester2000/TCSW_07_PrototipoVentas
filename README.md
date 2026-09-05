# Sistema de gestión de Ventas - Tecnologías para la construcción de software

**Java JDK:** 11
**Apache Maven:** 3.9.16

## Estructura del Dominio
**'Producto'**: Representa un artículo a añadirse al sistema, se implementó un encampsulamiento para que su código y nombre no puedan estar vacíos.

**'DetalleVenta'**: Modela la línea de venta enlazada a un producto, asegurando que la cantidad solicitada no supere al stock disponible.

**'Venta'**: Gestiona el conjunto de detalles de la transacción y calcula el monto total acumulado.