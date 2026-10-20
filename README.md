# API REST para la Administración de Usuarios

Proyecto de la **Unidad I** de la asignatura *Introducción a Aplicaciones Web Progresivas (PWA)*.

| | |
|---|---|
| **Alumno** | José Andrés Gutiérrez Vargas — No. de control 1223100413 |
| **Carrera** | Ingeniería en Tecnologías de la Información y Comunicación |
| **Profesor** | Francisco Hasaní Barrera Portilla |
| **Universidad** | Universidad Tecnológica del Norte de Guanajuato (UTNG) |
| **Cuatrimestre** | Septiembre – Diciembre |

---

## 1. Descripción del proyecto

API REST para la administración de usuarios construida con **Java 21**, **Spring Boot 3.3.5**,
**Spring Data JPA** y **PostgreSQL**. Expone las cuatro operaciones de alta, baja, consulta y
modificación, además de una búsqueda dinámica que combina cualquier subconjunto de criterios
en una sola consulta.

El proyecto mantiene una separación estricta de responsabilidades entre las capas de
controlador, servicio, repositorio y acceso a datos; recibe y devuelve la información mediante
DTOs; valida con anotaciones de Bean Validation y centraliza el manejo de errores en un único
manejador global.

### Restricciones de diseño que cumple la implementación

| # | Restricción | Cómo se cumple |
|---|---|---|
| 1 | No utilizar `@PathVariable` | Ningún endpoint recibe datos por la ruta. |
| 2 | No utilizar `@RequestParam` | Los criterios, filtros y la paginación viajan en el cuerpo. |
| 3 | Recibir los datos con `@RequestBody` | Los seis endpoints usan Request DTOs. |
| 4 | Ejecutar las validaciones con `@Valid` | Todos los cuerpos recibidos están anotados con `@Valid`. |
| 5 | Evitar lógica de negocio en los controladores | `UsuarioController` solo delega y traduce a HTTP. |
| 6 | No devolver entidades JPA | Siempre se responde con Response DTOs. |
| 7 | No repetir validaciones manuales | Las reglas viven en las anotaciones de los DTOs. |
| 8 | No manejar excepciones en cada controlador | `GlobalExceptionHandler` las centraliza. |
| 9 | No crear un endpoint por criterio | Una sola `Specification` resuelve todas las combinaciones. |
| 10 | Búsquedas dinámicas con Spring Data JPA | `JpaSpecificationExecutor` + `Specification<Usuario>`. |
| 11 | Códigos HTTP adecuados | 200, 201, 400, 404, 405, 409, 415 y 500. |
| 12 | No exponer información sensible | Las trazas solo se escriben en la bitácora del servidor. |
| 13 | No implementar consulta por ID | No existe ningún endpoint que reciba el identificador. |
| 14 | Llave compuesta nombre + correo | Es el criterio de consulta, actualización y baja. |

---

## 2. Tecnologías utilizadas

| Componente | Versión | Función |
|---|---|---|
| Java (JDK) | 21 | Lenguaje y plataforma de ejecución |
| Spring Boot | 3.3.5 | Framework base y autoconfiguración |
| Spring Web MVC | 6.1.x | Controladores REST |
| Spring Data JPA | 3.3.x | Persistencia y consultas dinámicas |
| Hibernate | 6.5.x | Implementación de JPA |
| Hibernate Validator | 8.x | Bean Validation (Jakarta Validation 3.0) |
| PostgreSQL | 14 o superior | Base de datos relacional |
| springdoc-openapi | 2.6.0 | Documentación Swagger / OpenAPI 3 |
| Maven | 3.9.x | Gestión de dependencias y construcción |
| JUnit 5 + MockMvc + H2 | — | Pruebas automatizadas de los endpoints |

---

## 3. Configuración de PostgreSQL

### 3.1 Crear la base de datos

```sql
CREATE DATABASE administracion_usuarios
    WITH ENCODING = 'UTF8' TEMPLATE = template0;
```

### 3.2 Crear el esquema

```bash
psql -U postgres -d administracion_usuarios -f scripts/01-esquema-postgresql.sql
psql -U postgres -d administracion_usuarios -f scripts/02-datos-prueba.sql   # opcional
```

### 3.3 Estructura de la tabla `usuarios`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `BIGINT IDENTITY` | Llave primaria generada por la base de datos |
| `nombre` | `VARCHAR(120) NOT NULL` | Nombre del usuario |
| `correo` | `VARCHAR(150) NOT NULL UNIQUE` | Correo electrónico único |
| `telefono` | `VARCHAR(20) NOT NULL` | Diez dígitos |
| `activo` | `BOOLEAN NOT NULL` | Estado del usuario |
| `fecha_creacion` | `TIMESTAMP NOT NULL` | Controlada por el servidor |
| `fecha_actualizacion` | `TIMESTAMP` | Controlada por el servidor |
| `fecha_baja` | `TIMESTAMP` | Momento de la baja lógica |

Restricciones declaradas en la base de datos: `uk_usuarios_correo` (UNIQUE),
`ck_usuarios_telefono_formato`, `ck_usuarios_correo_formato`, `ck_usuarios_nombre_no_vacio`
y `ck_usuarios_baja_coherente`. **Las validaciones de la aplicación no sustituyen a estas
restricciones: ambas capas se complementan.**

### 3.4 Credenciales

Se definen en `src/main/resources/application.yml` y pueden sobrescribirse con variables de
entorno sin tocar el código:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/administracion_usuarios
export DB_USUARIO=postgres
export DB_CLAVE=postgres
```

---

## 4. Instrucciones de ejecución

```bash
# 1. Compilar y ejecutar las pruebas
mvn clean verify

# 2. Levantar la aplicación
mvn spring-boot:run

# 3. (Alternativa) Generar y ejecutar el JAR
mvn clean package
java -jar target/administracion-usuarios-api-1.0.0.jar
```

Una vez iniciada la aplicación:

| Recurso | URL |
|---|---|
| Swagger UI | <http://localhost:8080/swagger-ui.html> |
| Contrato OpenAPI | <http://localhost:8080/v3/api-docs> |
| Base de la API | <http://localhost:8080/api/v1/usuarios> |

---

## 5. Endpoints implementados

| Operación | Método | Ruta | Éxito | Errores |
|---|---|---|---|---|
| Crear usuario | `POST` | `/api/v1/usuarios` | `201` | `400`, `409` |
| Listar usuarios | `GET` | `/api/v1/usuarios` | `200` | — |
| Consultar por llave compuesta | `POST` | `/api/v1/usuarios/consulta` | `200` | `400`, `404` |
| Búsqueda dinámica | `POST` | `/api/v1/usuarios/busqueda` | `200` | `400` |
| Actualización parcial | `PATCH` | `/api/v1/usuarios/actualizacion` | `200` | `400`, `404`, `409` |
| Baja de usuario | `DELETE` | `/api/v1/usuarios/eliminacion` | `200` | `400`, `404` |

**Ningún endpoint recibe información por la ruta ni por la cadena de consulta.**

### Códigos de error de negocio

| Código | HTTP | Situación |
|---|---|---|
| `DATOS_INVALIDOS` | 400 | Falla alguna anotación de Bean Validation |
| `JSON_INVALIDO` | 400 | El cuerpo no es un JSON válido |
| `CRITERIO_INVALIDO` | 400 | Criterio de ordenamiento no permitido o actualización vacía |
| `CAMPO_NO_ANULABLE` | 400 | Se envió `null` explícito en un campo obligatorio |
| `USUARIO_NO_ENCONTRADO` | 404 | No hay coincidencia con la llave compuesta |
| `RECURSO_NO_ENCONTRADO` | 404 | La ruta solicitada no existe |
| `METODO_NO_PERMITIDO` | 405 | Método HTTP incorrecto para la ruta |
| `CORREO_DUPLICADO` | 409 | El correo ya pertenece a otro usuario |
| `CONFLICTO_INTEGRIDAD` | 409 | Violación de una restricción de la base de datos |
| `TIPO_CONTENIDO_NO_SOPORTADO` | 415 | `Content-Type` distinto de `application/json` |
| `ERROR_INTERNO` | 500 | Error no previsto, registrado en la bitácora |

---

## 6. Ejemplos de peticiones y respuestas

### 6.1 Crear usuario — `POST /api/v1/usuarios`

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.perez@correo.com",
  "telefono": "4771234567"
}
```

**201 Created**

```json
{
  "mensaje": "Usuario registrado correctamente.",
  "datos": {
    "id": 1,
    "nombre": "Juan Pérez",
    "correo": "juan.perez@correo.com",
    "telefono": "4771234567",
    "activo": true,
    "fechaCreacion": "2026-10-14T10:30:00"
  }
}
```

**409 Conflict** (correo repetido)

```json
{
  "codigo": "CORREO_DUPLICADO",
  "mensaje": "El correo ya se encuentra registrado. Intente con otro correo.",
  "ruta": "/api/v1/usuarios",
  "marcaTiempo": "2026-10-14T10:31:12"
}
```

**400 Bad Request** (errores de validación)

```json
{
  "codigo": "DATOS_INVALIDOS",
  "mensaje": "Verifique la información capturada e intente nuevamente.",
  "errores": {
    "nombre": "El nombre es obligatorio y no puede contener solo espacios.",
    "correo": "El correo electrónico no tiene un formato válido.",
    "telefono": "El teléfono debe contener exactamente 10 dígitos."
  },
  "ruta": "/api/v1/usuarios",
  "marcaTiempo": "2026-10-14T10:32:05"
}
```

### 6.2 Listar usuarios — `GET /api/v1/usuarios`

**200 OK**

```json
{
  "mensaje": "Consulta realizada correctamente.",
  "datos": [
    {
      "id": 1,
      "nombre": "Juan Pérez",
      "correo": "juan.perez@correo.com",
      "telefono": "4771234567",
      "activo": true,
      "fechaCreacion": "2026-10-14T10:30:00"
    }
  ],
  "paginacion": {
    "pagina": 0,
    "tamanio": 10,
    "elementosEnPagina": 1,
    "totalElementos": 1,
    "totalPaginas": 1,
    "primera": true,
    "ultima": true
  }
}
```

### 6.3 Consultar por llave compuesta — `POST /api/v1/usuarios/consulta`

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.perez@correo.com"
}
```

**200 OK**

```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "correo": "juan.perez@correo.com",
  "telefono": "4771234567",
  "activo": true,
  "fechaCreacion": "2026-10-14T10:30:00"
}
```

**404 Not Found**

```json
{
  "codigo": "USUARIO_NO_ENCONTRADO",
  "mensaje": "No existe un usuario con el nombre y correo electrónico proporcionados.",
  "ruta": "/api/v1/usuarios/consulta",
  "marcaTiempo": "2026-10-14T10:35:40"
}
```

La consulta se resuelve con Spring Data JPA y parámetros con nombre, equivalente a:

```sql
SELECT * FROM usuarios WHERE nombre = :nombre AND correo = :correo;
```

### 6.4 Búsqueda dinámica — `POST /api/v1/usuarios/busqueda`

```json
{
  "nombre": "Juan",
  "correo": null,
  "telefono": null,
  "activo": true,
  "paginacion": { "pagina": 0, "tamanio": 5 },
  "orden": { "campos": ["nombre"], "ascendente": true }
}
```

Los campos nulos u omitidos **no participan** en la consulta. Reglas de coincidencia:

| Criterio | Tipo de coincidencia |
|---|---|
| `nombre` | Parcial, insensible a mayúsculas |
| `correo` | Parcial, insensible a mayúsculas |
| `telefono` | Parcial |
| `activo` | Exacta |
| `rangoFechaCreacion` | Rango inclusivo, ambos extremos opcionales |

### 6.5 Actualización parcial — `PATCH /api/v1/usuarios/actualizacion`

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.perez@correo.com",
  "telefono": "4779876543",
  "activo": true
}
```

`nombre` y `correo` **localizan** al usuario; para cambiar esos mismos datos se usan
`nuevoNombre` y `nuevoCorreo`. De este modo la llave de búsqueda y el valor nuevo nunca
se confunden.

Comportamiento frente a los tres estados posibles de un campo:

| Situación en el JSON | Comportamiento |
|---|---|
| El campo se omite | No se modifica |
| El campo llega con `null` | `400 CAMPO_NO_ANULABLE` |
| El campo llega con valor | Se valida y se aplica |

### 6.6 Baja — `DELETE /api/v1/usuarios/eliminacion`

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.perez@correo.com"
}
```

**Decisión del proyecto: la eliminación es lógica.** El registro se conserva, `activo` pasa a
`false` y se registra `fecha_baja`. La eliminación física solo se ejecuta cuando la petición
incluye `"eliminacionFisica": true`. Se responde `200 OK` con una respuesta estructurada.

---

## 7. Explicación del consumo de servicios REST

Todas las operaciones se realizan consumiendo la API por HTTP; no existe acceso directo a la
base de datos desde el cliente.

1. **Transporte.** El cliente envía `Content-Type: application/json` y recibe JSON.
2. **Entrada.** El controlador recibe el cuerpo con `@RequestBody` y lo deserializa en un
   Request DTO. `@Valid` ejecuta las validaciones declaradas en el DTO *antes* de que el
   método del controlador se ejecute.
3. **Delegación.** El controlador llama a la interfaz `UsuarioService`. No conoce la clase
   que la implementa ni contiene reglas de negocio.
4. **Negocio.** `UsuarioServiceImpl` aplica las reglas y, cuando alguna no se cumple, lanza
   una subclase de `NegocioException`.
5. **Persistencia.** `UsuarioRepository` ejecuta las consultas con Spring Data JPA. La
   búsqueda dinámica se arma con `UsuarioSpecification`, por lo que el filtrado ocurre en la
   base de datos y nunca en memoria.
6. **Salida.** La entidad se convierte en un Response DTO con `UsuarioMapper`. El controlador
   envuelve el resultado y asigna el código HTTP.
7. **Errores.** Cualquier excepción es interceptada por `GlobalExceptionHandler`, que devuelve
   siempre la misma estructura `ApiErrorResponse`.

Clientes utilizados para consumir la API: **Swagger UI**, **Postman**
(`pruebas/coleccion-postman.json`), el archivo `pruebas/usuarios.http` y el script
`scripts/pruebas-endpoints.sh`.

Ejemplo con `curl`:

```bash
curl -X POST http://localhost:8080/api/v1/usuarios \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Juan Pérez","correo":"juan.perez@correo.com","telefono":"4771234567"}'
```

---

## 8. Evidencia de pruebas realizadas

### 8.1 Pruebas automatizadas

```bash
mvn test
```

`UsuarioApiIntegrationTest` cubre: alta correcta, datos inválidos, correo duplicado, consulta
por llave compuesta (éxito y 404), actualización parcial, rechazo del `null` explícito, baja
lógica, búsqueda dinámica y listado paginado.

### 8.2 Pruebas manuales

| Archivo | Uso |
|---|---|
| `pruebas/usuarios.http` | 24 casos para VS Code (REST Client) o IntelliJ |
| `pruebas/coleccion-postman.json` | 21 peticiones organizadas en 5 carpetas |
| `scripts/pruebas-endpoints.sh` | Ejecuta los 21 casos y genera `pruebas/evidencia-pruebas.txt` |

```bash
bash scripts/pruebas-endpoints.sh
```

El script compara el código HTTP obtenido contra el esperado e imprime `OK` o `FALLA` por caso.

---

## 9. Diagramas

| Archivo | Contenido |
|---|---|
| `docs/diagrama-de-clases.png` · `.pdf` · `.svg` | Diagrama de clases completo |
| `docs/diagrama-herencia-entidades.png` | Jerarquía `EntidadAuditable → Persona → Usuario` |
| `docs/diagrama-polimorfismo-excepciones.png` | Polimorfismo en el manejo de errores |
| `docs/diagrama-interfaces-realizaciones.png` | Interfaces y sus implementaciones |
| `docs/diagrama-herencia-polimorfismo.png` | Vista combinada de los tres conceptos |
| `docs/diagrama-arquitectura-capas.png` | Arquitectura en capas |
| `docs/diagrama-entidad-relacion.png` | Modelo de datos y mapeo objeto-relacional |
| `docs/diagrama-secuencia-registro.png` | Secuencia del alta, con el caso de correo duplicado |

### Conceptos de programación orientada a objetos representados

| Concepto | Dónde está en el código |
|---|---|
| **Clases abstractas** | `EntidadAuditable`, `Persona`, `ServicioBase<E, ID>`, `NegocioException` |
| **Interfaces** | `UsuarioService`, `UsuarioRepository`, `Mapeador<E, R>`, `Specification<Usuario>` |
| **Herencia** | `Usuario → Persona → EntidadAuditable`; 4 excepciones → `NegocioException`; `UsuarioServiceImpl → ServicioBase` |
| **Polimorfismo** | `GlobalExceptionHandler.manejarNegocio(NegocioException)` resuelve 404/409/400 según la subclase; `ServicioBase.persistir()` invoca `descripcionAuditoria()` sin conocer el tipo real |
| **Encapsulamiento** | Atributos privados con acceso controlado; los campos del servidor nunca se exponen en los Request DTO |
| **Genéricos** | `ServicioBase<E, ID>`, `Mapeador<E, R>`, `RespuestaApi<T>`, `RespuestaPaginada<T>` |

---

## 10. Estructura del proyecto

```
administracion-usuarios-api
├── pom.xml
├── README.md
├── docs/                                  diagramas en PNG y PDF
├── scripts/
│   ├── 01-esquema-postgresql.sql
│   ├── 02-datos-prueba.sql
│   └── pruebas-endpoints.sh
├── pruebas/
│   ├── usuarios.http
│   └── coleccion-postman.json
└── src
    ├── main/java/com/utng/usuarios
    │   ├── AdministracionUsuariosApplication.java
    │   ├── config/        OpenApiConfig · PropiedadesPaginacion
    │   ├── controller/    UsuarioController
    │   ├── service/       UsuarioService · ServicioBase · impl/UsuarioServiceImpl
    │   ├── repository/    UsuarioRepository
    │   ├── entity/        EntidadAuditable · Persona · Usuario
    │   ├── dto/request/   8 Request DTOs
    │   ├── dto/response/  5 Response DTOs
    │   ├── exception/     GlobalExceptionHandler · NegocioException + 4 subclases
    │   ├── mapper/        Mapeador · UsuarioMapper
    │   └── specification/ UsuarioSpecification
    ├── main/resources/application.yml
    └── test/java/com/utng/usuarios/UsuarioApiIntegrationTest.java
```

---

## 11. Calidad del servicio

| Aspecto | Implementación |
|---|---|
| **Disponibilidad** | Arquitectura en capas, código desacoplado mediante interfaces y pool de conexiones HikariCP |
| **Rendimiento** | Paginación obligatoria con tope de 50 registros, índices de apoyo, filtrado en la base de datos y `open-in-view: false` |
| **Respuesta inmediata** | Validación antes de persistir, verificación de unicidad previa al INSERT y mensajes de error claros |
| **Mantenibilidad** | DTOs separados por operación, manejo de errores centralizado, propiedades externalizadas y documentación Swagger generada del propio código |
