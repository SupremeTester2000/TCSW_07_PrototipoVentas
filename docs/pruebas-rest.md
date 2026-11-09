

\# Guía de reproducción de pruebas REST



\## Requisitos



\- Java 11.

\- Maven.

\- PostgreSQL configurado de acuerdo con el README del proyecto.

\- Node.js y npm para validar OpenAPI con Redocly CLI.



\## 1. Ejecutar las pruebas automatizadas



Desde la raíz del repositorio:



```bash

mvn clean test

```



Las pruebas utilizan JUnit y MockMvc para verificar las operaciones

REST sin requerir que el servidor permanezca ejecutándose.



Los escenarios cubiertos incluyen:



\- GET de productos exitoso.

\- POST de productos exitoso.

\- PUT de productos exitoso.

\- POST de ventas exitoso.

\- Entrada inválida.

\- Producto inexistente.

\- JSON mal formado.

\- Error inesperado sin exposición de información interna.

\- Configuración de orígenes CORS.

\- Correspondencia estructural entre operaciones y contrato OpenAPI.



\## 2. Validar el contrato OpenAPI



Ejecutar:



```bash

npx --yes @redocly/cli lint docs/openapi.yaml

```



El documento debe terminar con el mensaje:



`Your API description is valid.`



Las advertencias informativas de Redocly deben revisarse y justificarse.

No deben ocultarse mediante archivos de exclusión.



\## 3. Iniciar la aplicación



Configurar PostgreSQL y las variables de entorno necesarias siguiendo

el README principal.



Después ejecutar:



```bash

mvn spring-boot:run

```



El servidor local utiliza el puerto 8080.



\## 4. Solicitudes HTTP reproducibles



Consultar productos:



```bash

curl -i http://localhost:8080/api/products

```



Registrar un producto:



```bash

curl -i -X POST http://localhost:8080/api/products \\

\-H "Content-Type: application/json" \\

\-d '{"name":"Café","price":12.50,"stock":4}'

```



Actualizar un producto:



```bash

curl -i -X PUT http://localhost:8080/api/products/P00001 \\

\-H "Content-Type: application/json" \\

\-d '{"name":"Café actualizado","price":13.00,"stock":5}'

```



Enviar una solicitud inválida:



```bash

curl -i -X POST http://localhost:8080/api/products \\

\-H "Content-Type: application/json" \\

\-d '{"name":" ","price":-1,"stock":-1}'

```



Procesar una venta:



```bash

curl -i -X POST http://localhost:8080/api/sales \\

\-H "Content-Type: application/json" \\

\-d '{"P00001":2}'

```



Para comprobar un producto inexistente, utilizar un código válido

que no exista en la base de datos.



\## 5. Comprobación CORS



Configurar un origen explícitamente autorizado para la prueba:



```bash

export CORS\_ALLOWED\_ORIGINS=http://localhost:5173

```



Iniciar o reiniciar la aplicación y ejecutar una solicitud preflight:



```bash

curl -i -X OPTIONS http://localhost:8080/api/products \\

\-H "Origin: http://localhost:5173" \\

\-H "Access-Control-Request-Method: GET"

```



Para comprobar un origen no autorizado, realizar la misma solicitud

utilizando un origen diferente.



La respuesta no debe incluir `Access-Control-Allow-Origin`

para el origen no autorizado.



El puerto 5173 es únicamente un origen ilustrativo para reproducir

la configuración; no representa un frontend existente del proyecto.



\## 6. Evidencia



Conservar las siguientes evidencias:



\- Salida completa de `mvn clean test`.

\- Resultado de Redocly CLI.

\- Solicitudes y respuestas HTTP reproducidas.

\- Comprobación de CORS.

\- Rama, commit y Pull Request correspondiente.
