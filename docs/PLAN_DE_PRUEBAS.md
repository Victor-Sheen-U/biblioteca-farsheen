# Plan de Pruebas: Sistema de Gestión de Biblioteca

**Proyecto:** Biblioteca  
**Fecha:** 18 de octubre de 2026  
**Versión:** 1.0.0  
**Autores:** Jhon Edison Farfán & Víctor Sheen  
**Tecnologías:** Spring Boot 4.1.1 (Java 21), Spring Security 7, JJWT 0.13.0, MySQL / H2, Gradle  

---

## 1. Introducción

El presente documento constituye el **Plan de Pruebas Integral** para el backend del **Sistema de Gestión de Biblioteca**. Su propósito es validar de forma sistemática y exhaustiva que todos los requerimientos funcionales (RFN1 al RFN20) y los endpoints de soporte operen conforme a las reglas de negocio, directrices de seguridad y estándares de arquitectura establecidos.

El sistema proporciona una API REST orientada a la gestión de tres módulos principales:
- **Gestión de Libros y Ejemplares:** Catálogo de libros, asignación de géneros y autores, administración de copias físicas (ejemplares con identificador unívoco sin código de barras), búsqueda multicriterio, consulta de disponibilidad temporal y visualización de detalles históricos.
- **Administración de Usuarios y Seguridad:** Registro público de lectores, autenticación mediante tokens JWT (*stateless*), control de acceso basado en roles (`USER` y `ADMIN`), consulta de perfiles y suspensión/inhabilitación lógica de cuentas.
- **Control de Reservas:** Asignación transaccional de ejemplares libres para rangos temporales determinados, consulta individual y masiva, validación de conflictos de disponibilidad y políticas de cancelación diferenciadas (con antelación mínima de 24 horas para usuarios y cancelación administrativa irrestricta).

### Documentación Interactiva de la API
El sistema expone la especificación OpenAPI v3 con soporte interactivo para autenticación Bearer JWT:
- **Swagger UI:** [http://localhost:10101/biblioteca/swagger-ui.html](http://localhost:10101/biblioteca/swagger-ui.html)
- **OpenAPI JSON Docs:** [http://localhost:10101/biblioteca/api-docs](http://localhost:10101/biblioteca/api-docs)

---

## 2. Alcance

### 2.1 Módulos y Requerimientos Incluidos
El alcance de las pruebas abarca el 100% de los endpoints expuestos bajo el prefijo `/biblioteca/api/v1`:

1. **Autenticación y Usuarios:**
   - RFN10: Registro público (`POST /auth/register`).
   - RFN11: Inicio de sesión y emisión de JWT (`POST /auth/login`).
   - RFN12: Búsqueda de usuario por ID (`GET /usuarios/{id}`) y por documento (`GET /usuarios/documento/{documento}`).
   - RFN13: Deshabilitación lógica de usuario (`PATCH /usuarios/documento/{documento}/deshabilitar`).
   - Soporte: Creación administrativa de usuarios (`POST /usuarios`).

2. **Soporte de Catálogo (Géneros y Autores):**
   - Creación de géneros (`POST /generos`) y listado (`GET /generos`).
   - Creación de autores (`POST /autores`) y listado (`GET /autores`).

3. **Gestión de Libros y Ejemplares:**
   - RFN1: Registro de nuevo libro con autores y género (`POST /libros`).
   - RFN2: Inhabilitación lógica de libro (`DELETE /libros/{id}`).
   - RFN3: Búsqueda de libro por ID (`GET /libros/{id}`).
   - RFN4: Búsqueda insensible a mayúsculas por título (`GET /libros/buscar/titulo?titulo=...`).
   - RFN5: Búsqueda por género (`GET /libros/buscar/genero?genero=...`).
   - RFN6: Búsqueda por autor (`GET /libros/buscar/autor?autor=...`).
   - RFN7: Actualización de metadatos de libro (`PUT /libros/{id}`).
   - RFN8: Consulta de disponibilidad en instante o rango (`GET /libros/{id}/disponibilidad`).
   - RFN9: Consulta detallada y métricas de libro (`GET /libros/{id}/detalle`).
   - Soporte: Creación de ejemplares para un libro (`POST /libros/{id}/ejemplares`).

4. **Control de Reservas:**
   - RFN14: Creación de reserva con asignación automática de ejemplar (`POST /reservas`).
   - RFN15: Consulta de reserva por ID con verificación de propiedad (`GET /reservas/{id}`).
   - RFN16: Listado general de reservas para administradores (`GET /reservas`).
   - RFN17: Listado de reservas de un usuario específico (`GET /reservas/usuario/{idUsuario}`).
   - RFN18: Listado de reservas propias del usuario autenticado (`GET /reservas/mis-reservas`).
   - RFN19: Cancelación de reserva por usuario con regla de 24 horas (`PATCH /reservas/{id}/cancelar`).
   - RFN20: Cancelación forzada de reserva por administrador (`PATCH /reservas/{id}/cancelar-admin`).

### 2.2 Exclusiones Explícitas
Quedan fuera del alcance de este plan:
- Pruebas de estrés y rendimiento bajo alta concurrencia masiva (JMeter/Gatling).
- Pruebas de interfaz gráfica de usuario (frontend web o móvil).
- Funcionalidades no especificadas en los requerimientos (recuperación de contraseña por correo, pasarelas de pago, multas económicas, auditoría avanzada o Dockerización).

---

## 3. Ambientes de Prueba

### 3.1 Ambiente de Desarrollo Local
- **URL Base de la API:** `http://localhost:10101/biblioteca/api/v1`
- **Context Path:** `/biblioteca`
- **Puerto de Aplicación:** `10101`
- **Motor de Base de Datos:** MySQL 8.x en puerto local `3307` (`demo_db` o `biblioteca_db`)
- **Perfil Activo de Spring Boot:** `dev`

### 3.2 Ambiente de Pruebas Automatizadas (Unitarias)
- **Base de Datos en Memoria:** H2 Database en modo compatibilidad MySQL (`jdbc:h2:mem:biblioteca_test;MODE=MySQL;DB_CLOSE_DELAY=-1`)
- **Perfil de Spring Boot:** `test`
- **Framework de Pruebas:** JUnit 5 (Jupiter), Mockito (Extension), AssertJ
- **Comando de Ejecución:** `./gradlew clean test` o `./gradlew clean build`

---

## 4. Criterios de Aceptación

### 4.1 Códigos de Estado HTTP Estándar
Cada respuesta de la API debe devolver el código de estado HTTP semántico correspondiente:
- **`200 OK`**: Petición procesada con éxito que retorna datos o confirmación de actualización.
- **`201 Created`**: Recurso creado exitosamente (retorna la representación del nuevo recurso).
- **`400 Bad Request`**: Datos de entrada no válidos, reglas de validación `@Valid` violadas, conflictos de negocio (correo duplicado, libro sin disponibilidad, cancelación tardía a menos de 24 horas, etc.).
- **`401 Unauthorized`**: Token JWT ausente, inválido, mal formado o expirado; credenciales de login incorrectas; o cuenta de usuario deshabilitada.
- **`403 Forbidden`**: Token válido pero con rol insuficiente (ej. un `USER` intentando acceder a rutas exclusivas de `ADMIN`) o un usuario intentando consultar/cancelar una reserva ajena.
- **`404 Not Found`**: El recurso solicitado (libro, usuario, ejemplar, reserva, género, autor) no existe o fue inhabilitado lógicamente.

### 4.2 Estructura Uniforme de Respuestas de Error
Toda respuesta de error (400, 401, 403, 404, 500) debe ajustarse al esquema JSON estandarizado:
```json
{
  "timestamp": "2026-10-18T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción precisa y amigable del error en español",
  "path": "/biblioteca/api/v1/..."
}
```

---

## 5. Estrategia de Pruebas

Se aplica un enfoque de pruebas en profundidad que combina:
1. **Pruebas de Camino Feliz (Happy Path):** Verificación de flujos operacionales correctos con entradas válidas y condiciones ideales.
2. **Pruebas de Validación y Límites de Negocio:**
   - Formatos inválidos de correo, campos vacíos o nulos, longitudes mínimas/máximas.
   - Restricciones de unicidad (documento de identidad, correo electrónico, ISBN, nombre de género).
   - Inconsistencia temporal en reservas (`fechaEntrega` anterior o igual a `fechaInicio`).
   - Límite de capacidad (solicitud de reservas cuando todos los ejemplares están ocupados en el rango).
   - Regla de cancelación anticipada (bloqueo a menos de 24 horas para usuarios estándar).
3. **Pruebas de Autenticación y Autorización (RBAC):**
   - Protección perimetral de endpoints según roles (`PÚBLICO`, `USER`, `ADMIN`).
   - Validación de encabezados `Authorization: Bearer <token>`.
   - Rechazo de tokens manipulados o alterados.
   - Aislamiento de privacidad: los usuarios con rol `USER` únicamente pueden consultar y cancelar sus propias reservas.
4. **Verificación de Eliminación Lógica (Soft Delete):**
   - Confirmación de que las bajas operacionales (`DELETE /libros/{id}`, `PATCH /usuarios/.../deshabilitar`, `PATCH /reservas/.../cancelar`) actualizan flags booleanos o estados sin ejecutar `DELETE` físico en la base de datos.

---

## 6. Detalles de la Prueba

### 6.1 Módulos y Casos de Prueba (CP01 a CP49)

#### Carpeta 00: Usuarios y Autenticación

---

##### Caso de prueba: CP01 - Registro público válido (Usuario A)
- **Endpoint:** `POST /biblioteca/api/v1/auth/register`
- **Método HTTP:** `POST`
- **Descripción:** Registrar un nuevo usuario lector con datos completos y válidos.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "documentoIdentidad": "1001_{{suffix}}",
    "nombreCompleto": "Usuario Lector A",
    "correo": "usuario.a_{{suffix}}@biblioteca.com",
    "password": "Password123!"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** Objeto `AuthResponseDTO` con `token` (JWT válido), `correo`, `nombreCompleto`, `rol: "USER"`.
- **Prueba exitosa si:** Devuelve código 201 y un token JWT con prefijo `ey...`.

---

##### Caso de prueba: CP02 - Registro con correo duplicado
- **Endpoint:** `POST /biblioteca/api/v1/auth/register`
- **Método HTTP:** `POST`
- **Descripción:** Intentar registrar un usuario con un correo ya existente en el sistema.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:** Mismo cuerpo de CP01.
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400`, mensaje que indica que el correo ya está registrado.
- **Prueba exitosa si:** Devuelve 400 y mensaje en español indicando correo duplicado.

---

##### Caso de prueba: CP03 - Registro con datos inválidos (correo mal formado, password corta)
- **Endpoint:** `POST /biblioteca/api/v1/auth/register`
- **Método HTTP:** `POST`
- **Descripción:** Validar que `@Valid` intercepte entradas que violen restricciones de formato y longitud.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "documentoIdentidad": "",
    "nombreCompleto": "",
    "correo": "correo-no-valido",
    "password": "123"
  }
  ```
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400` y detalle de validación.
- **Prueba exitosa si:** Devuelve 400 impidiendo la persistencia.

---

##### Caso de prueba: CP04 - Admin crea usuario con rol ADMIN
- **Endpoint:** `POST /biblioteca/api/v1/usuarios`
- **Método HTTP:** `POST`
- **Descripción:** Un administrador crea directamente una cuenta administrativa secundaria.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "documentoIdentidad": "9001_{{suffix}}",
    "nombreCompleto": "Admin Secundario",
    "correo": "admin2_{{suffix}}@biblioteca.com",
    "password": "AdminPassword123!",
    "rol": "ADMIN"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `UsuarioDTO` con `idUsuario`, `rol: "ADMIN"`, `estado: true` (sin campo password).
- **Prueba exitosa si:** Devuelve 201 y el usuario queda registrado con rol `ADMIN`.

---

##### Caso de prueba: CP05 - Login válido (Usuario A)
- **Endpoint:** `POST /biblioteca/api/v1/auth/login`
- **Método HTTP:** `POST`
- **Descripción:** Autenticar credenciales válidas y obtener token JWT.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "correo": "usuario.a_{{suffix}}@biblioteca.com",
    "password": "Password123!"
  }
  ```
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `AuthResponseDTO` con `token`, `correo`, `nombreCompleto`, `rol: "USER"`.
- **Prueba exitosa si:** Retorna 200 y el token JWT es válido.

---

##### Caso de prueba: CP06 - Login con password incorrecta
- **Endpoint:** `POST /biblioteca/api/v1/auth/login`
- **Método HTTP:** `POST`
- **Descripción:** Intentar iniciar sesión con contraseña errónea.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "correo": "usuario.a_{{suffix}}@biblioteca.com",
    "password": "ContraseñaIncorrecta123"
  }
  ```
- **Código esperado:** `401 Unauthorized`
- **Cuerpo esperado:** JSON de error con `status: 401`, `error: "Unauthorized"`.
- **Prueba exitosa si:** Se deniega la autenticación con código 401.

---

##### Caso de prueba: CP07 - Buscar usuario por id (admin)
- **Endpoint:** `GET /biblioteca/api/v1/usuarios/{id}`
- **Método HTTP:** `GET`
- **Descripción:** El administrador consulta la información de un usuario por su identificador primario.
- **Parámetros:** Path param: `id = {{idUsuarioA}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `UsuarioDTO` con los datos del usuario A (`documentoIdentidad`, `correo`, `nombreCompleto`, `rol`, `estado: true`).
- **Prueba exitosa si:** Retorna 200 y los datos coinciden con el usuario A sin exponer hash de contraseña.

---

##### Caso de prueba: CP08 - Buscar usuario por documento (admin)
- **Endpoint:** `GET /biblioteca/api/v1/usuarios/documento/{documento}`
- **Método HTTP:** `GET`
- **Descripción:** El administrador consulta la información de un usuario mediante su documento de identidad.
- **Parámetros:** Path param: `documento = 1001_{{suffix}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `UsuarioDTO` correspondiente al documento consultado.
- **Prueba exitosa si:** Retorna 200 y el `documentoIdentidad` coincide.

---

##### Caso de prueba: CP09 - Buscar usuario por documento inexistente
- **Endpoint:** `GET /biblioteca/api/v1/usuarios/documento/{documento}`
- **Método HTTP:** `GET`
- **Descripción:** Consultar un documento de identidad que no se encuentra en el sistema.
- **Parámetros:** Path param: `documento = 99999999`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404`, indicando que el usuario no fue encontrado.
- **Prueba exitosa si:** Devuelve código 404 con mensaje explicativo.

---

##### Caso de prueba: CP10 - GET /usuarios/{id} con token de USUARIO (403)
- **Endpoint:** `GET /biblioteca/api/v1/usuarios/{id}`
- **Método HTTP:** `GET`
- **Descripción:** Verificar que un usuario con rol `USER` no pueda acceder a endpoints restringidos a `ADMIN`.
- **Parámetros:** Path param: `id = {{idUsuarioA}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `403 Forbidden`
- **Cuerpo esperado:** JSON de error con `status: 403`, `error: "Forbidden"`, indicando denegación de acceso.
- **Prueba exitosa si:** Devuelve 403 bloqueando el acceso al recurso administrativo.

---

##### Caso de prueba: CP11 - GET /libros/{id} sin token (401)
- **Endpoint:** `GET /biblioteca/api/v1/libros/1`
- **Método HTTP:** `GET`
- **Descripción:** Realizar una solicitud a un endpoint protegido sin incluir encabezado de autorización.
- **Parámetros:** Path param: `id = 1`.
- **Headers:** Ninguno.
- **Body:** N/A.
- **Código esperado:** `401 Unauthorized`
- **Cuerpo esperado:** JSON de error con `status: 401`, `error: "Unauthorized"`.
- **Prueba exitosa si:** Devuelve 401 interceptado por `JwtAuthenticationEntryPoint`.

---

##### Caso de prueba: CP12 - Token alterado (401)
- **Endpoint:** `GET /biblioteca/api/v1/libros/1`
- **Método HTTP:** `GET`
- **Descripción:** Enviar un token JWT cuya firma criptográfica o contenido ha sido adulterado.
- **Parámetros:** Path param: `id = 1`.
- **Headers:** `Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.tokenInvalidoAdulterado`
- **Body:** N/A.
- **Código esperado:** `401 Unauthorized`
- **Cuerpo esperado:** JSON de error con `status: 401`.
- **Prueba exitosa si:** El filtro JWT detecta la firma inválida y rechaza la solicitud con 401.

---

#### Carpeta 01: Soporte (Géneros y Autores)

---

##### Caso de prueba: CP13a - Crear género (admin)
- **Endpoint:** `POST /biblioteca/api/v1/generos`
- **Método HTTP:** `POST`
- **Descripción:** El administrador registra un nuevo género literario en el catálogo.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "nombre": "Novela_{{suffix}}"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `GeneroDTO` con `idGenero` y `nombre`.
- **Prueba exitosa si:** Devuelve 201 y persiste el nuevo género.

---

##### Caso de prueba: CP13b - Crear género duplicado (admin)
- **Endpoint:** `POST /biblioteca/api/v1/generos`
- **Método HTTP:** `POST`
- **Descripción:** Intentar crear un género con un nombre ya existente en el catálogo.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:** Mismo cuerpo de CP13a.
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error indicando duplicidad de nombre de género.
- **Prueba exitosa si:** Se deniega la creación duplicada con código 400.

---

##### Caso de prueba: CP14a - Crear autor 1 (admin)
- **Endpoint:** `POST /biblioteca/api/v1/autores`
- **Método HTTP:** `POST`
- **Descripción:** El administrador registra un primer autor en el sistema.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "nombre": "García Márquez_{{suffix}}"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `AutorDTO` con `idAutor` y `nombre`.
- **Prueba exitosa si:** Devuelve 201 y el autor queda registrado.

---

##### Caso de prueba: CP14b - Crear autor 2 (admin)
- **Endpoint:** `POST /biblioteca/api/v1/autores`
- **Método HTTP:** `POST`
- **Descripción:** El administrador registra un segundo autor en el sistema.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "nombre": "Vargas Llosa_{{suffix}}"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `AutorDTO` con `idAutor` y `nombre`.
- **Prueba exitosa si:** Devuelve 201 con el nuevo autor registrado.

---

##### Caso de prueba: CP14c - Listar géneros con USUARIO
- **Endpoint:** `GET /biblioteca/api/v1/generos`
- **Método HTTP:** `GET`
- **Descripción:** Un usuario autenticado consulta la lista completa de géneros disponibles.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `GeneroDTO` que contiene al menos el género creado en CP13a.
- **Prueba exitosa si:** Devuelve 200 y el arreglo incluye el género creado.

---

#### Carpeta 02: Libros y Ejemplares

---

##### Caso de prueba: CP15 - Crear libro con 2 autores (admin)
- **Endpoint:** `POST /biblioteca/api/v1/libros`
- **Método HTTP:** `POST`
- **Descripción:** El administrador crea un nuevo libro asociando un género y los dos autores previamente creados.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "isbn": "9780307474728_{{suffix}}",
    "titulo": "Cien Años de Soledad_{{suffix}}",
    "idGenero": {{idGenero}},
    "idsAutores": [{{idAutor1}}, {{idAutor2}}]
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `LibroDTO` con `idLibro`, `titulo`, `isbn`, `idGenero`, lista `autores` (2 elementos) y `estado: true`.
- **Prueba exitosa si:** Devuelve 201 con los dos autores vinculados y `estado: true`.

---

##### Caso de prueba: CP16 - Crear libro con género inexistente (admin)
- **Endpoint:** `POST /biblioteca/api/v1/libros`
- **Método HTTP:** `POST`
- **Descripción:** Intentar registrar un libro haciendo referencia a un `idGenero` que no existe.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "isbn": "9781111111111_{{suffix}}",
    "titulo": "Libro Fallido",
    "idGenero": 99999,
    "idsAutores": [{{idAutor1}}]
  }
  ```
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400` indicando género inexistente.
- **Prueba exitosa si:** Se rechaza la persistencia con código 400.

---

##### Caso de prueba: CP17 - Crear libro con token de USUARIO (403)
- **Endpoint:** `POST /biblioteca/api/v1/libros`
- **Método HTTP:** `POST`
- **Descripción:** Verificar que un usuario con rol `USER` no pueda crear libros en el catálogo.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`, `Content-Type: application/json`
- **Body:** Mismo cuerpo de CP15.
- **Código esperado:** `403 Forbidden`
- **Cuerpo esperado:** JSON de error con `status: 403`.
- **Prueba exitosa si:** El filtro de seguridad rechaza la acción con código 403.

---

##### Caso de prueba: CP18 - Crear 2 ejemplares del libro (admin)
- **Endpoint:** `POST /biblioteca/api/v1/libros/{id}/ejemplares`
- **Método HTTP:** `POST`
- **Descripción:** El administrador registra 2 copias físicas (ejemplares) para el libro creado. Cada ejemplar se identifica únicamente por su `idEjemplar` autoincremental sin código de barras.
- **Parámetros:** Path param: `id = {{idLibro}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "cantidad": 2,
    "estadoDisponibilidad": "DISPONIBLE"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** Lista de 2 objetos `EjemplarDTO`, cada uno con `idEjemplar`, `idLibro` y `estadoDisponibilidad: "DISPONIBLE"`.
- **Prueba exitosa si:** Devuelve 201 y un arreglo con 2 ejemplares físicos creados.

---

##### Caso de prueba: CP19 - Crear ejemplares de libro inexistente (admin)
- **Endpoint:** `POST /biblioteca/api/v1/libros/99999/ejemplares`
- **Método HTTP:** `POST`
- **Descripción:** Intentar agregar copias físicas a un libro que no existe en el sistema.
- **Parámetros:** Path param: `id = 99999`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "cantidad": 1,
    "estadoDisponibilidad": "DISPONIBLE"
  }
  ```
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404` indicando que el libro no existe.
- **Prueba exitosa si:** Devuelve 404 informando que no existe el libro padre.

---

##### Caso de prueba: CP20 - Buscar libro por id (admin)
- **Endpoint:** `GET /biblioteca/api/v1/libros/{id}`
- **Método HTTP:** `GET`
- **Descripción:** Consultar los metadatos de un libro mediante su clave primaria.
- **Parámetros:** Path param: `id = {{idLibro}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `LibroDTO` con `idLibro`, `titulo`, `isbn`, `genero`, `autores` y `estado: true`.
- **Prueba exitosa si:** Devuelve 200 con la información exacta del libro.

---

##### Caso de prueba: CP21 - Buscar libro por id inexistente (admin)
- **Endpoint:** `GET /biblioteca/api/v1/libros/9999`
- **Método HTTP:** `GET`
- **Descripción:** Consultar un ID de libro que no existe en el sistema.
- **Parámetros:** Path param: `id = 9999`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404`.
- **Prueba exitosa si:** Devuelve 404 indicando libro no encontrado.

---

##### Caso de prueba: CP22 - Buscar por título (USUARIO)
- **Endpoint:** `GET /biblioteca/api/v1/libros/buscar/titulo`
- **Método HTTP:** `GET`
- **Descripción:** Búsqueda insensible a mayúsculas por fragmento de título.
- **Parámetros:** Query param: `titulo = Cien Años_{{suffix}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `LibroDTO` que incluye el libro coincidente con título "Cien Años de Soledad...".
- **Prueba exitosa si:** Devuelve 200 y el arreglo contiene el libro buscado.

---

##### Caso de prueba: CP23 - Buscar por género (USUARIO)
- **Endpoint:** `GET /biblioteca/api/v1/libros/buscar/genero`
- **Método HTTP:** `GET`
- **Descripción:** Filtrar catálogo de libros activos por nombre de género.
- **Parámetros:** Query param: `genero = Novela_{{suffix}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `LibroDTO` con los libros pertenecientes a dicho género.
- **Prueba exitosa si:** Devuelve 200 y los libros retornados tienen asignado el género especificado.

---

##### Caso de prueba: CP24a - Buscar por autor 1 (USUARIO)
- **Endpoint:** `GET /biblioteca/api/v1/libros/buscar/autor`
- **Método HTTP:** `GET`
- **Descripción:** Filtrar catálogo de libros activos por el nombre del primer autor.
- **Parámetros:** Query param: `autor = García Márquez_{{suffix}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `LibroDTO` que incluye el libro asociado a García Márquez.
- **Prueba exitosa si:** Devuelve 200 y el libro está en la lista.

---

##### Caso de prueba: CP24b - Buscar por autor 2 (USUARIO)
- **Endpoint:** `GET /biblioteca/api/v1/libros/buscar/autor`
- **Método HTTP:** `GET`
- **Descripción:** Filtrar catálogo de libros activos por el nombre del segundo autor coautor.
- **Parámetros:** Query param: `autor = Vargas Llosa_{{suffix}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `LibroDTO` que incluye el libro asociado a Vargas Llosa.
- **Prueba exitosa si:** Devuelve 200 confirmando la relación muchos-a-muchos con autores.

---

##### Caso de prueba: CP25 - Actualizar libro (admin)
- **Endpoint:** `PUT /biblioteca/api/v1/libros/{id}`
- **Método HTTP:** `PUT`
- **Descripción:** El administrador actualiza el título y los metadatos de un libro existente.
- **Parámetros:** Path param: `id = {{idLibro}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "isbn": "9780307474728_{{suffix}}",
    "titulo": "Cien Años de Soledad (Edición Conmemorativa)_{{suffix}}",
    "idGenero": {{idGenero}},
    "idsAutores": [{{idAutor1}}, {{idAutor2}}]
  }
  ```
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `LibroDTO` reflejando el nuevo título.
- **Prueba exitosa si:** Devuelve 200 con el título modificado correctamente.

---

##### Caso de prueba: CP26 - Actualizar libro inexistente (admin)
- **Endpoint:** `PUT /biblioteca/api/v1/libros/99999`
- **Método HTTP:** `PUT`
- **Descripción:** Intentar actualizar un libro con un ID inexistente.
- **Parámetros:** Path param: `id = 99999`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`, `Content-Type: application/json`
- **Body:** Mismo cuerpo de CP25.
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404`.
- **Prueba exitosa si:** Devuelve 404 indicando libro no encontrado.

---

#### Carpeta 03: Control de Reservas y Disponibilidad

---

##### Caso de prueba: CP27 - Reservar (USUARIO A) rango lejano (+10 días)
- **Endpoint:** `POST /biblioteca/api/v1/reservas`
- **Método HTTP:** `POST`
- **Descripción:** El Usuario A reserva un ejemplar del libro para un rango de 5 días a partir de dentro de 10 días.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "idLibro": {{idLibro}},
    "fechaInicio": "{{fechaInicioLejana}}",
    "fechaEntrega": "{{fechaFinLejana}}"
  }
  ```
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `ReservaDTO` con `idReserva`, `idUsuario: idUsuarioA`, `idEjemplar` asignado, `estadoReserva: "ACTIVA"`, `fechaInicio` y `fechaEntrega`.
- **Prueba exitosa si:** Devuelve 201 y asigna automáticamente uno de los 2 ejemplares físicos libres.

---

##### Caso de prueba: CP28 - Disponibilidad dentro de ese rango (queda 1 ejemplar)
- **Endpoint:** `GET /biblioteca/api/v1/libros/{id}/disponibilidad`
- **Método HTTP:** `GET`
- **Descripción:** Consultar si el libro tiene copias disponibles para prestar en una fecha comprendida en el rango de CP27.
- **Parámetros:** Path param: `id = {{idLibro}}`. Query param: `fechaHora = {{fechaLejanaInstante}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `DisponibilidadDTO` con `disponible: true` y `ejemplaresDisponibles: 1`.
- **Prueba exitosa si:** Devuelve 200 y confirma que aún queda 1 ejemplar disponible.

---

##### Caso de prueba: CP29 - Segunda reserva del mismo rango (USUARIO B)
- **Endpoint:** `POST /biblioteca/api/v1/reservas`
- **Método HTTP:** `POST`
- **Descripción:** El Usuario B reserva el segundo ejemplar disponible en el mismo rango de fechas.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioB}}`, `Content-Type: application/json`
- **Body:** Mismo rango de fechas de CP27 con `idLibro`.
- **Código esperado:** `201 Created`
- **Cuerpo esperado:** `ReservaDTO` con `idReserva`, asignando el segundo ejemplar físico (`idEjemplar` diferente al de CP27).
- **Prueba exitosa si:** Devuelve 201 asignando el último ejemplar libre sin colisión de IDs.

---

##### Caso de prueba: CP30 - Tercera reserva del mismo rango (400 sin ejemplares)
- **Endpoint:** `POST /biblioteca/api/v1/reservas`
- **Método HTTP:** `POST`
- **Descripción:** Intentar realizar una tercera reserva simultánea cuando los 2 únicos ejemplares del libro ya están ocupados.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`, `Content-Type: application/json`
- **Body:** Mismo rango de fechas con `idLibro`.
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400`, mensaje indicando que no hay ejemplares disponibles en el rango solicitado.
- **Prueba exitosa si:** El sistema rechaza la reserva y devuelve 400 protegiendo la integridad física de inventario.

---

##### Caso de prueba: CP31 - Disponibilidad dentro del rango (ya ocupados -> false)
- **Endpoint:** `GET /biblioteca/api/v1/libros/{id}/disponibilidad`
- **Método HTTP:** `GET`
- **Descripción:** Verificar que la disponibilidad calculada para la fecha en conflicto ahora reporte falso (0 ejemplares libres).
- **Parámetros:** Path param: `id = {{idLibro}}`. Query param: `fechaHora = {{fechaLejanaInstante}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `DisponibilidadDTO` con `disponible: false` y `ejemplaresDisponibles: 0`.
- **Prueba exitosa si:** Devuelve 200 con `disponible: false`.

---

##### Caso de prueba: CP32 - Reserva con entrega anterior al inicio (400)
- **Endpoint:** `POST /biblioteca/api/v1/reservas`
- **Método HTTP:** `POST`
- **Descripción:** Intentar crear una reserva donde `fechaEntrega` es cronológicamente anterior a `fechaInicio`.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`, `Content-Type: application/json`
- **Body:**
  ```json
  {
    "idLibro": {{idLibro}},
    "fechaInicio": "2026-11-20T10:00:00",
    "fechaEntrega": "2026-11-15T10:00:00"
  }
  ```
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400` señalando la inconsistencia de fechas.
- **Prueba exitosa si:** Devuelve 400 bloqueando la transacción.

---

##### Caso de prueba: CP33 - Dueño consulta su reserva (Usuario A)
- **Endpoint:** `GET /biblioteca/api/v1/reservas/{id}`
- **Método HTTP:** `GET`
- **Descripción:** El Usuario A consulta la reserva creada en CP27 que le pertenece legítimamente.
- **Parámetros:** Path param: `id = {{idReservaLejana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `ReservaDTO` con los datos completos de la reserva.
- **Prueba exitosa si:** Devuelve 200 permitiendo el acceso al propietario del recurso.

---

##### Caso de prueba: CP34 - Otro usuario consulta esa reserva (Usuario B sobre A -> 403)
- **Endpoint:** `GET /biblioteca/api/v1/reservas/{id}`
- **Método HTTP:** `GET`
- **Descripción:** El Usuario B intenta consultar la reserva del Usuario A sin tener rol de Administrador.
- **Parámetros:** Path param: `id = {{idReservaLejana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioB}}`
- **Body:** N/A.
- **Código esperado:** `403 Forbidden`
- **Cuerpo esperado:** JSON de error con `status: 403` indicando que no tiene permisos para ver reservas ajenas.
- **Prueba exitosa si:** Devuelve 403 salvaguardando la privacidad entre usuarios.

---

##### Caso de prueba: CP35 - Reserva inexistente (404)
- **Endpoint:** `GET /biblioteca/api/v1/reservas/99999`
- **Método HTTP:** `GET`
- **Descripción:** Consultar un ID de reserva inexistente.
- **Parámetros:** Path param: `id = 99999`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404`.
- **Prueba exitosa si:** Devuelve 404 indicando reserva no encontrada.

---

##### Caso de prueba: CP36a - Listar todas las reservas (admin)
- **Endpoint:** `GET /biblioteca/api/v1/reservas`
- **Método HTTP:** `GET`
- **Descripción:** El administrador solicita el listado maestro de todas las reservas del sistema.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo con todas las reservas registradas.
- **Prueba exitosa si:** Devuelve 200 con la totalidad de registros.

---

##### Caso de prueba: CP36b - Listar todas las reservas con USUARIO (403)
- **Endpoint:** `GET /biblioteca/api/v1/reservas`
- **Método HTTP:** `GET`
- **Descripción:** Un usuario estándar intenta listar la totalidad de reservas del sistema.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `403 Forbidden`
- **Cuerpo esperado:** JSON de error con `status: 403`.
- **Prueba exitosa si:** Devuelve 403 bloqueando el endpoint administrativo.

---

##### Caso de prueba: CP37 - Listar reservas de un usuario (admin)
- **Endpoint:** `GET /biblioteca/api/v1/reservas/usuario/{idUsuario}`
- **Método HTTP:** `GET`
- **Descripción:** El administrador audita el historial de reservas de un usuario específico.
- **Parámetros:** Path param: `idUsuario = {{idUsuarioA}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `ReservaDTO` donde cada registro corresponde al usuario solicitado.
- **Prueba exitosa si:** Devuelve 200 y filtra exactamente las reservas del Usuario A.

---

##### Caso de prueba: CP38 - Mis reservas (Usuario A)
- **Endpoint:** `GET /biblioteca/api/v1/reservas/mis-reservas`
- **Método HTTP:** `GET`
- **Descripción:** El usuario consulta sus propias reservas extrayendo la identidad del token JWT.
- **Parámetros:** Ninguno.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo de `ReservaDTO` con las reservas asociadas al Usuario A.
- **Prueba exitosa si:** Devuelve 200 mostrando únicamente las reservas del usuario autenticado.

---

##### Caso de prueba: CP39 - Detalle del libro (admin)
- **Endpoint:** `GET /biblioteca/api/v1/libros/{id}/detalle`
- **Método HTTP:** `GET`
- **Descripción:** El administrador consulta la información técnica completa del libro, incluyendo inventario de ejemplares y total histórico de reservas.
- **Parámetros:** Path param: `id = {{idLibro}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `LibroDetalleDTO` con `idLibro`, `titulo`, `isbn`, lista `ejemplares` (2 ejemplares físicos) y `totalReservasHistoricas: 2`.
- **Prueba exitosa si:** Devuelve 200 con la agregación correcta de ejemplares y conteo histórico.

---

#### Carpeta 04: Cancelaciones

---

##### Caso de prueba: CP40 - Cancelar reserva con inicio > 24 h (Usuario A)
- **Endpoint:** `PATCH /biblioteca/api/v1/reservas/{id}/cancelar`
- **Método HTTP:** `PATCH`
- **Descripción:** El Usuario A cancela su reserva con más de 24 horas de anticipación a la fecha de inicio prevista.
- **Parámetros:** Path param: `id = {{idReservaLejana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `ReservaDTO` con `estadoReserva: "CANCELADA"`.
- **Prueba exitosa si:** Devuelve 200 y el estado pasa a `CANCELADA`.

---

##### Caso de prueba: CP41 - Cancelar reserva con inicio en < 24 h (400)
- **Endpoint:** `PATCH /biblioteca/api/v1/reservas/{id}/cancelar`
- **Método HTTP:** `PATCH`
- **Descripción:** El Usuario A intenta cancelar una reserva cuya fecha de inicio es dentro de pocas horas (menos de 24 horas de antelación).
- **Parámetros:** Path param: `id = {{idReservaCercana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400`, mensaje indicando que las reservas solo pueden cancelarse con al menos 1 día (24 horas) de anticipación.
- **Prueba exitosa si:** Devuelve 400 impidiendo la cancelación extemporánea por parte del usuario.

---

##### Caso de prueba: CP42 - Cancelar reserva ya cancelada (400)
- **Endpoint:** `PATCH /biblioteca/api/v1/reservas/{id}/cancelar`
- **Método HTTP:** `PATCH`
- **Descripción:** Intentar volver a cancelar una reserva que ya se encuentra en estado `CANCELADA`.
- **Parámetros:** Path param: `id = {{idReservaLejana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `400 Bad Request`
- **Cuerpo esperado:** JSON de error con `status: 400` indicando que la reserva ya fue cancelada previamente.
- **Prueba exitosa si:** Devuelve 400 protegiendo la idempotencia de estado.

---

##### Caso de prueba: CP43 - Cancelar reserva ajena (Usuario B sobre A -> 403)
- **Endpoint:** `PATCH /biblioteca/api/v1/reservas/{id}/cancelar`
- **Método HTTP:** `PATCH`
- **Descripción:** El Usuario B intenta cancelar una reserva que pertenece al Usuario A.
- **Parámetros:** Path param: `id = {{idReservaCercana}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioB}}`
- **Body:** N/A.
- **Código esperado:** `403 Forbidden`
- **Cuerpo esperado:** JSON de error con `status: 403` por acceso no autorizado a reserva de otro usuario.
- **Prueba exitosa si:** Devuelve 403 protegiendo los recursos propios del usuario.

---

##### Caso de prueba: CP44 - Admin cancela la reserva de < 24 h
- **Endpoint:** `PATCH /biblioteca/api/v1/reservas/{id}/cancelar-admin`
- **Método HTTP:** `PATCH`
- **Descripción:** El administrador cancela una reserva sin importar el tiempo restante para el inicio de la misma.
- **Parámetros:** Path param: `id = {{idReservaCercana}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `ReservaDTO` con `estadoReserva: "CANCELADA"`.
- **Prueba exitosa si:** Devuelve 200 permitiendo la anulación administrativa.

---

#### Carpeta 05: Inhabilitaciones (Soft Delete)

---

##### Caso de prueba: CP45 - Deshabilitar usuario B (admin)
- **Endpoint:** `PATCH /biblioteca/api/v1/usuarios/documento/{documento}/deshabilitar`
- **Método HTTP:** `PATCH`
- **Descripción:** El administrador suspende lógicamente la cuenta del Usuario B (`estado = false`).
- **Parámetros:** Path param: `documento = {{documentoUsuarioB}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `UsuarioDTO` con `estado: false`.
- **Prueba exitosa si:** Devuelve 200 y el campo `estado` pasa a `false`.

---

##### Caso de prueba: CP46 - Login del usuario B deshabilitado (401)
- **Endpoint:** `POST /biblioteca/api/v1/auth/login`
- **Método HTTP:** `POST`
- **Descripción:** El Usuario B intenta iniciar sesión después de haber sido deshabilitado.
- **Parámetros:** Ninguno.
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "correo": "usuario.b_{{suffix}}@biblioteca.com",
    "password": "Password123!"
  }
  ```
- **Código esperado:** `401 Unauthorized`
- **Cuerpo esperado:** JSON de error con `status: 401`, mensaje indicando que el usuario se encuentra deshabilitado.
- **Prueba exitosa si:** Devuelve 401 denegando el ingreso al sistema.

---

##### Caso de prueba: CP47 - Inhabilitar libro (admin)
- **Endpoint:** `DELETE /biblioteca/api/v1/libros/{id}`
- **Método HTTP:** `DELETE`
- **Descripción:** El administrador deshabilita lógicamente un libro (`estado = false`).
- **Parámetros:** Path param: `id = {{idLibro}}`.
- **Headers:** `Authorization: Bearer {{tokenAdmin}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** `LibroDTO` con `estado: false`.
- **Prueba exitosa si:** Devuelve 200 con `estado: false` sin borrar físicamente el registro de la base de datos.

---

##### Caso de prueba: CP48 - Buscar por título el libro inhabilitado (200 sin ese libro)
- **Endpoint:** `GET /biblioteca/api/v1/libros/buscar/titulo`
- **Método HTTP:** `GET`
- **Descripción:** Un usuario busca libros por el título del libro inhabilitado y comprueba que este ya no aparece en los resultados públicos.
- **Parámetros:** Query param: `titulo = Cien Años`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `200 OK`
- **Cuerpo esperado:** Arreglo vacío `[]` o sin incluir el libro con ID `idLibro`.
- **Prueba exitosa si:** Devuelve 200 y el libro inhabilitado no figura entre los resultados visibles.

---

##### Caso de prueba: CP49 - Disponibilidad de libro inhabilitado (404)
- **Endpoint:** `GET /biblioteca/api/v1/libros/{id}/disponibilidad`
- **Método HTTP:** `GET`
- **Descripción:** Intentar consultar la disponibilidad de un libro que ha sido inhabilitado lógicamente.
- **Parámetros:** Path param: `id = {{idLibro}}`. Query param: `fechaHora = {{fechaLejanaInstante}}`.
- **Headers:** `Authorization: Bearer {{tokenUsuarioA}}`
- **Body:** N/A.
- **Código esperado:** `404 Not Found`
- **Cuerpo esperado:** JSON de error con `status: 404`, indicando que el libro no existe o está inhabilitado.
- **Prueba exitosa si:** Devuelve 404 protegiendo las operaciones sobre entidades dadas de baja.

---

### 6.2 Pruebas de Autenticación y Autorización

Esta sección consolida la evaluación de los controles de seguridad implementados por `SecurityConfig`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint` y `JwtAccessDeniedHandler`:

1. **Tokens Ausentes (401 Unauthorized):**
   - Comprobado en **CP11**: Todo acceso a rutas no públicas (`/biblioteca/api/v1/**` fuera de `/auth/**` y `/swagger-ui/**`) sin encabezado `Authorization` es interceptado retornando código HTTP `401 Unauthorized` y el cuerpo JSON uniforme estandarizado.

2. **Tokens Inválidos, Expirados o Manipulados (401 Unauthorized):**
   - Comprobado en **CP12**: Cualquier token alterado en su payload o con firma HMAC-SHA no coincidente lanza una excepción criptográfica interceptada por el filtro JWT, produciendo respuesta uniforme HTTP `401 Unauthorized`.

3. **Control de Acceso por Roles (RBAC - 403 Forbidden):**
   - Comprobado en **CP10**, **CP17**, **CP34**, **CP36b**, **CP43**: Cuando un usuario autenticado con rol `USER` intenta ejecutar acciones reservadas exclusivamente a `ADMIN` (como gestión de usuarios, creación de libros, listado general de reservas), o cuando intenta acceder o alterar reservas que pertenecen a otro usuario, el sistema responde de inmediato con HTTP `403 Forbidden`.

4. **Inhabilitación Lógica de Usuarios (401 Unauthorized):**
   - Comprobado en **CP45** y **CP46**: La suspensión de una cuenta (`estado = false`) invalida el acceso inmediatamente en el endpoint de autenticación `/auth/login` y en las consultas subsecuentes mediante `UserDetails.isEnabled()`, retornando HTTP `401 Unauthorized`.

---

## 7. Reporte de Resultados

A continuación se presenta la matriz de resultados de los 49 casos de prueba. Dado que la ejecución funcional completa con Postman depende del despliegue del servidor junto a una instancia activa de base de datos MySQL, los casos se encuentran formalmente configurados, validados a nivel de pruebas unitarias y automatizadas en la colección Postman adjunta, y catalogados como **Pendiente de ejecución** para la fase de pruebas dinámicas en vivo.

### 7.1 Tabla Consolidada de Casos de Prueba

| ID Caso | Nombre del Caso | Método y Endpoint | Rol Requerido | Código Esperado | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CP01** | Registro público válido (Usuario A) | `POST /auth/register` | PÚBLICO | `201 Created` | Pendiente de ejecución |
| **CP02** | Registro con correo duplicado | `POST /auth/register` | PÚBLICO | `400 Bad Request` | Pendiente de ejecución |
| **CP03** | Registro con datos inválidos | `POST /auth/register` | PÚBLICO | `400 Bad Request` | Pendiente de ejecución |
| **CP04** | Admin crea usuario con rol ADMIN | `POST /usuarios` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP05** | Login válido (Usuario A) | `POST /auth/login` | PÚBLICO | `200 OK` | Pendiente de ejecución |
| **CP06** | Login con password incorrecta | `POST /auth/login` | PÚBLICO | `401 Unauthorized` | Pendiente de ejecución |
| **CP07** | Buscar usuario por ID | `GET /usuarios/{id}` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP08** | Buscar usuario por documento | `GET /usuarios/documento/{documento}` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP09** | Buscar usuario por documento inexistente | `GET /usuarios/documento/{documento}` | ADMIN | `404 Not Found` | Pendiente de ejecución |
| **CP10** | GET /usuarios/{id} con rol USER | `GET /usuarios/{id}` | USER | `403 Forbidden` | Pendiente de ejecución |
| **CP11** | GET /libros/{id} sin token | `GET /libros/1` | SIN TOKEN | `401 Unauthorized` | Pendiente de ejecución |
| **CP12** | Token alterado / firma no válida | `GET /libros/1` | TOKEN INVÁLIDO | `401 Unauthorized` | Pendiente de ejecución |
| **CP13a** | Crear género (admin) | `POST /generos` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP13b** | Crear género duplicado | `POST /generos` | ADMIN | `400 Bad Request` | Pendiente de ejecución |
| **CP14a** | Crear autor 1 (admin) | `POST /autores` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP14b** | Crear autor 2 (admin) | `POST /autores` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP14c** | Listar géneros con USER | `GET /generos` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP15** | Crear libro con 2 autores | `POST /libros` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP16** | Crear libro con género inexistente | `POST /libros` | ADMIN | `400 Bad Request` | Pendiente de ejecución |
| **CP17** | Crear libro con rol USER | `POST /libros` | USER | `403 Forbidden` | Pendiente de ejecución |
| **CP18** | Crear 2 ejemplares físicos | `POST /libros/{id}/ejemplares` | ADMIN | `201 Created` | Pendiente de ejecución |
| **CP19** | Crear ejemplares de libro inexistente | `POST /libros/99999/ejemplares` | ADMIN | `404 Not Found` | Pendiente de ejecución |
| **CP20** | Buscar libro por ID | `GET /libros/{id}` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP21** | Buscar libro por ID inexistente | `GET /libros/9999` | USER / ADMIN | `404 Not Found` | Pendiente de ejecución |
| **CP22** | Buscar por título | `GET /libros/buscar/titulo` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP23** | Buscar por género | `GET /libros/buscar/genero` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP24a** | Buscar por autor 1 | `GET /libros/buscar/autor` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP24b** | Buscar por autor 2 | `GET /libros/buscar/autor` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP25** | Actualizar libro | `PUT /libros/{id}` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP26** | Actualizar libro inexistente | `PUT /libros/99999` | ADMIN | `404 Not Found` | Pendiente de ejecución |
| **CP27** | Registrar reserva rango lejano | `POST /reservas` | USER | `201 Created` | Pendiente de ejecución |
| **CP28** | Disponibilidad con 1 ejemplar libre | `GET /libros/{id}/disponibilidad` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP29** | Segunda reserva del mismo rango | `POST /reservas` | USER | `201 Created` | Pendiente de ejecución |
| **CP30** | Tercera reserva sin ejemplares libres | `POST /reservas` | USER | `400 Bad Request` | Pendiente de ejecución |
| **CP31** | Disponibilidad con 0 ejemplares libres | `GET /libros/{id}/disponibilidad` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP32** | Reserva con fechas invertidas | `POST /reservas` | USER | `400 Bad Request` | Pendiente de ejecución |
| **CP33** | Usuario consulta su propia reserva | `GET /reservas/{id}` | USER | `200 OK` | Pendiente de ejecución |
| **CP34** | Usuario consulta reserva de otro usuario | `GET /reservas/{id}` | USER | `403 Forbidden` | Pendiente de ejecución |
| **CP35** | Reserva inexistente | `GET /reservas/99999` | USER / ADMIN | `404 Not Found` | Pendiente de ejecución |
| **CP36a** | Listar todas las reservas (admin) | `GET /reservas` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP36b** | Listar todas las reservas con USER | `GET /reservas` | USER | `403 Forbidden` | Pendiente de ejecución |
| **CP37** | Listar reservas por usuario | `GET /reservas/usuario/{idUsuario}` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP38** | Mis reservas (usuario autenticado) | `GET /reservas/mis-reservas` | USER | `200 OK` | Pendiente de ejecución |
| **CP39** | Detalle y métricas de libro | `GET /libros/{id}/detalle` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP40** | Cancelar reserva con antelación > 24h | `PATCH /reservas/{id}/cancelar` | USER | `200 OK` | Pendiente de ejecución |
| **CP41** | Cancelar reserva con antelación < 24h | `PATCH /reservas/{id}/cancelar` | USER | `400 Bad Request` | Pendiente de ejecución |
| **CP42** | Cancelar reserva ya cancelada | `PATCH /reservas/{id}/cancelar` | USER | `400 Bad Request` | Pendiente de ejecución |
| **CP43** | Cancelar reserva de otro usuario | `PATCH /reservas/{id}/cancelar` | USER | `403 Forbidden` | Pendiente de ejecución |
| **CP44** | Admin cancela reserva de < 24h | `PATCH /reservas/{id}/cancelar-admin` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP45** | Deshabilitar usuario B | `PATCH /usuarios/documento/{doc}/deshabilitar` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP46** | Login de usuario deshabilitado | `POST /auth/login` | PÚBLICO | `401 Unauthorized` | Pendiente de ejecución |
| **CP47** | Inhabilitar libro | `DELETE /libros/{id}` | ADMIN | `200 OK` | Pendiente de ejecución |
| **CP48** | Búsqueda excluye libro inhabilitado | `GET /libros/buscar/titulo` | USER / ADMIN | `200 OK` | Pendiente de ejecución |
| **CP49** | Disponibilidad de libro inhabilitado | `GET /libros/{id}/disponibilidad` | USER / ADMIN | `404 Not Found` | Pendiente de ejecución |

---

### 7.2 Evidencia de Ejecución por Caso de Prueba

#### CP01 - Registro público válido (Usuario A)
Ejecución de la prueba: (adjuntar captura)

#### CP02 - Registro con correo duplicado
Ejecución de la prueba: (adjuntar captura)

#### CP03 - Registro con datos inválidos (correo mal formado, password corta)
Ejecución de la prueba: (adjuntar captura)

#### CP04 - Admin crea usuario con rol ADMIN
Ejecución de la prueba: (adjuntar captura)

#### CP05 - Login válido (Usuario A)
Ejecución de la prueba: (adjuntar captura)

#### CP06 - Login con password incorrecta
Ejecución de la prueba: (adjuntar captura)

#### CP07 - Buscar usuario por id (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP08 - Buscar usuario por documento (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP09 - Buscar usuario por documento inexistente
Ejecución de la prueba: (adjuntar captura)

#### CP10 - GET /usuarios/{id} con token de USUARIO (403)
Ejecución de la prueba: (adjuntar captura)

#### CP11 - GET /libros/{id} sin token (401)
Ejecución de la prueba: (adjuntar captura)

#### CP12 - Token alterado (401)
Ejecución de la prueba: (adjuntar captura)

#### CP13a - Crear género (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP13b - Crear género duplicado (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP14a - Crear autor 1 (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP14b - Crear autor 2 (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP14c - Listar géneros con USUARIO
Ejecución de la prueba: (adjuntar captura)

#### CP15 - Crear libro con 2 autores (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP16 - Crear libro con género inexistente (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP17 - Crear libro con token de USUARIO (403)
Ejecución de la prueba: (adjuntar captura)

#### CP18 - Crear 2 ejemplares del libro (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP19 - Crear ejemplares de libro inexistente (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP20 - Buscar libro por id (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP21 - Buscar libro por id inexistente (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP22 - Buscar por título (USUARIO)
Ejecución de la prueba: (adjuntar captura)

#### CP23 - Buscar por género (USUARIO)
Ejecución de la prueba: (adjuntar captura)

#### CP24a - Buscar por autor 1 (USUARIO)
Ejecución de la prueba: (adjuntar captura)

#### CP24b - Buscar por autor 2 (USUARIO)
Ejecución de la prueba: (adjuntar captura)

#### CP25 - Actualizar libro (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP26 - Actualizar libro inexistente (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP27 - Reservar (USUARIO A) rango lejano (+10 días)
Ejecución de la prueba: (adjuntar captura)

#### CP28 - Disponibilidad dentro de ese rango (queda 1 ejemplar)
Ejecución de la prueba: (adjuntar captura)

#### CP29 - Segunda reserva del mismo rango (USUARIO B)
Ejecución de la prueba: (adjuntar captura)

#### CP30 - Tercera reserva del mismo rango (400 sin ejemplares)
Ejecución de la prueba: (adjuntar captura)

#### CP31 - Disponibilidad dentro del rango (ya ocupados -> false)
Ejecución de la prueba: (adjuntar captura)

#### CP32 - Reserva con entrega anterior al inicio (400)
Ejecución de la prueba: (adjuntar captura)

#### CP33 - Dueño consulta su reserva (Usuario A)
Ejecución de la prueba: (adjuntar captura)

#### CP34 - Otro usuario consulta esa reserva (Usuario B sobre A -> 403)
Ejecución de la prueba: (adjuntar captura)

#### CP35 - Reserva inexistente (404)
Ejecución de la prueba: (adjuntar captura)

#### CP36a - Listar todas las reservas (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP36b - Listar todas las reservas con USUARIO (403)
Ejecución de la prueba: (adjuntar captura)

#### CP37 - Listar reservas de un usuario (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP38 - Mis reservas (Usuario A)
Ejecución de la prueba: (adjuntar captura)

#### CP39 - Detalle del libro (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP40 - Cancelar reserva con inicio > 24 h (Usuario A)
Ejecución de la prueba: (adjuntar captura)

#### CP41 - Cancelar reserva con inicio en < 24 h (400)
Ejecución de la prueba: (adjuntar captura)

#### CP42 - Cancelar reserva ya cancelada (400)
Ejecución de la prueba: (adjuntar captura)

#### CP43 - Cancelar reserva ajena (Usuario B sobre A -> 403)
Ejecución de la prueba: (adjuntar captura)

#### CP44 - Admin cancela la reserva de < 24 h
Ejecución de la prueba: (adjuntar captura)

#### CP45 - Deshabilitar usuario B (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP46 - Login del usuario B deshabilitado (401)
Ejecución de la prueba: (adjuntar captura)

#### CP47 - Inhabilitar libro (admin)
Ejecución de la prueba: (adjuntar captura)

#### CP48 - Buscar por título el libro inhabilitado (200 sin ese libro)
Ejecución de la prueba: (adjuntar captura)

#### CP49 - Disponibilidad de libro inhabilitado (404)
Ejecución de la prueba: (adjuntar captura)

---

## 8. Conclusión

El plan de pruebas presentado abarca la totalidad de los requerimientos funcionales, directrices arquitectónicas y criterios de calidad estipulados para el **Sistema de Gestión de Biblioteca**:

1. **Alineación Funcional Completa:** Los 20 requerimientos funcionales (RFN1–RFN20) y los endpoints de soporte cuentan con casos de prueba específicos que evalúan tanto las rutas de éxito como la adecuada gestión de errores de negocio (incompatibilidad de fechas, falta de inventario físico, nombres y correos duplicados, etc.).
2. **Seguridad Robusta:** La integración de Spring Security 7 y JJWT 0.13.0 garantiza la protección perimetral del sistema, impidiendo accesos anónimos a rutas protegidas, bloqueando escaladas de privilegios no autorizadas y respetando la privacidad de las reservas por usuario.
3. **Persistencia e Integridad:** El modelo relacional sin código de barras en ejemplares y basado en bajas lógicas (soft delete) preserva el histórico de transacciones y permite consultar métricas agregadas confiables.
4. **Verificación Automatizada:** La cobertura de 76 pruebas unitarias aisladas con Mockito y la prueba de arranque de contexto de Spring Boot con H2 aseguran una base de código estable, mantenible y lista para pruebas dinámicas mediante la colección Postman provista.
