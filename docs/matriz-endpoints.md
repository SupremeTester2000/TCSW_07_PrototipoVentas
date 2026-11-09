

\# Matriz de endpoints REST



Esta matriz relaciona los métodos HTTP, las URI, los estados documentados

en OpenAPI y las pruebas automatizadas del proyecto.



| Método | URI | Estados HTTP documentados | Comprobación |

|---|---|---|---|

| GET | /api/products | 200, 500 | Consulta exitosa y error inesperado |

| POST | /api/products | 201, 400, 500 | Registro correcto y entrada inválida |

| PUT | /api/products/{code} | 200, 400, 404, 500 | Actualización, validación y producto inexistente |

| POST | /api/sales | 200, 400, 404, 500 | Venta correcta, solicitud inválida y producto inexistente |



\## Formato uniforme de error



Todas las excepciones REST controladas utilizan la estructura ApiError:



```json

{

&#x20; "title": "Producto inexistente",

&#x20; "status": 404,

&#x20; "detail": "No se encontró el producto solicitado"

}

```



\## Códigos HTTP



\- 200: operación exitosa.

\- 201: recurso creado.

\- 400: datos de entrada inválidos.

\- 404: recurso solicitado inexistente.

\- 500: error inesperado del servidor.



No se documenta HTTP 409 porque no se ha identificado

un conflicto de negocio real en las operaciones actuales.



\## CORS



La configuración se aplica a `/api/\*\*`.



Métodos permitidos: GET, POST y PUT.



Los orígenes se obtienen de la variable `CORS\_ALLOWED\_ORIGINS`.

Si no se configura ningún origen, no se autorizan solicitudes

de otros orígenes de navegador.



\## Relación con el contrato



El contrato de referencia está en `docs/openapi.yaml`.



La validación de sintaxis se realiza con Redocly CLI.

Las pruebas automatizadas se encuentran en `src/test/java`.
