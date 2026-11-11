# Biblioteca Farsheen — API REST (Spring Boot 4.1.1)

Backend oficial del **Sistema de Gestión de Biblioteca**, desarrollado bajo arquitectura en capas con **Spring Boot 4.1.1**, **Java 21**, **Spring Security 7 (JWT Stateless)**, **Spring Data JPA**, **MapStruct** y **OpenAPI 3 / Swagger UI**.

---

## 1. Stack Tecnológico

- **Lenguaje y Runtime:** Java 21 LTS
- **Framework Principal:** Spring Boot 4.1.1 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-security`)
- **Seguridad:** Autenticación y autorización *stateless* con JWT (`io.jsonwebtoken:jjwt:0.13.0`, HS256)
- **Mapeo DTO ↔ Entidad:** MapStruct 1.6.3 + Lombok
- **Base de Datos:**
  - Desarrollo / Producción: MySQL / MariaDB (XAMPP puerto `3307`, base `biblioteca`)
  - Pruebas / Demo en memoria: H2 Database en modo compatibilidad MySQL (`SPRING_PROFILES_ACTIVE=test`)
- **Documentación Interactiva:** `springdoc-openapi-starter-webmvc-ui:3.1.0`
- **Construcción:** Gradle 9.7.1 (`./gradlew`)

---

## 2. Arquitectura de Capas

El proyecto se organiza bajo el paquete base `com.eam.biblioteca`:

- `presentationLayer`: Controladores REST (`AuthController`, `CatalogoController`, `LibroController`, `EjemplarController`, `ReservaController`, `GeneroController`, `AutorController`, `UsuarioController`) y manejo centralizado de errores (`GlobalExceptionHandler`).
- `businessLayer`: Servicios de dominio (`*Service`, `*ServiceImpl`) y objetos de transferencia (`dto/`).
- `persistenceLayer`: Entidades JPA (`entity/`), repositorios Spring Data (`repository/`), DAOs (`dao/`) y mappers MapStruct (`mapper/`).
- `security`: Generación y validación de tokens (`JwtService`) y filtro de autenticación (`JwtAuthenticationFilter`).
- `config`: Configuración de seguridad y CORS (`SecurityConfig`), OpenAPI (`OpenApiConfig`) y sembrado de datos (`DataInitializer`, `DemoDataInitializer`).

---

## 3. Variables de Entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3307/biblioteca?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true` | URL JDBC para MySQL/MariaDB |
| `DB_USERNAME` | `root` | Usuario de la base de datos |
| `DB_PASSWORD` | *(vacío)* | Contraseña de la base de datos |
| `JWT_SECRET` | `ClaveSecretaPorDefectoParaDesarrolloLocalBiblioteca2026DebeTenerMasDe32Bytes!` | Clave HMAC-SHA256 (mínimo 32 bytes) |
| `JWT_EXPIRATION_MS` | `7200000` | Tiempo de expiración del token JWT en milisegundos (2 horas) |
| `ADMIN_DOCUMENTO` | `0000000000` | Documento del administrador inicial |
| `ADMIN_NOMBRE` | `Administrador Principal` | Nombre del administrador inicial |
| `ADMIN_CORREO` | `admin@biblioteca.com` | Correo del administrador inicial |
| `ADMIN_PASSWORD` | `Admin12345*` | Contraseña del administrador inicial |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200,http://127.0.0.1:4200` | Orígenes permitidos para peticiones CORS separados por coma |
| `SEED_DEMO_DATA` | `false` | Si es `true`, ejecuta `DemoDataInitializer` al arrancar (idempotente) |

---

## 4. Cómo Ejecutar el Proyecto

### Opción A — Con MySQL / MariaDB (XAMPP en puerto 3307)

1. Inicia el servicio MySQL/MariaDB en el puerto `3307`.
2. Ejecuta con datos de demostración habilitados:
   ```bash
   SEED_DEMO_DATA=true ./gradlew bootRun
   ```

### Opción B — Sin MySQL externo (Perfil `test` con H2 en memoria)

Ideal para desarrollo rápido del frontend o pruebas de integración autónomas:

```bash
SPRING_PROFILES_ACTIVE=test SEED_DEMO_DATA=true ./gradlew bootRun
```

### URLs del Servidor

- **URL Base de la API:** `http://localhost:10101/biblioteca/api/v1`
- **Swagger UI:** `http://localhost:10101/biblioteca/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:10101/biblioteca/api-docs`

---

## 5. Usuarios de Prueba

| Rol | Correo | Contraseña | Origen |
|---|---|---|---|
| `ADMIN` | `admin@biblioteca.com` | `Admin12345*` | Creado siempre por `DataInitializer` |
| `USUARIO` | `usuario@biblioteca.com` | `Usuario123*` | Creado cuando `SEED_DEMO_DATA=true` (`DemoDataInitializer`) |

---

## 6. Contrato con el Frontend (`biblio-front`)

El frontend Angular 22 se comunica exclusivamente por HTTP REST bajo la base `http://localhost:10101/biblioteca/api/v1`:

| Método y Ruta | Acceso | Cuerpo / Parámetros | Respuesta |
|---|---|---|---|
| `POST /auth/register` | Público | `UsuarioCreateDTO` (`documentoIdentidad`, `nombreCompleto`, `correo`, `password`) | `201 Created` (`UsuarioDTO` con `rol: "USUARIO"`) |
| `POST /auth/login` | Público | `LoginRequestDTO` (`correo`, `password`) | `200 OK` (`AuthResponseDTO`: `token`, `tipoToken`, `expiraEnMs`, `correo`, `rol`, `nombreCompleto`) |
| `GET /catalogo` | Público | — | `200 OK` (`LibroCatalogoDTO[]`: libros habilitados ordenados por título con `anioPublicacion`, `totalEjemplares` y `ejemplaresDisponibles`) |
| `GET /catalogo/{id}` | Público | `id` (path) | `200 OK` (`LibroCatalogoDTO`) · `404 Not Found` si no existe o está inhabilitado |
| `GET /libros/{id}/disponibilidad` | `USUARIO` / `ADMIN` | `fechaHora` (ISO local `YYYY-MM-DDTHH:mm:ss`) | `200 OK` (`DisponibilidadDTO`: `idLibro`, `fechaHora`, `disponible`, `ejemplaresDisponibles`) |
| `POST /reservas` | `USUARIO` / `ADMIN` | `ReservaCreateDTO` (`idLibro`, `fechaInicio`, `fechaEntrega` en ISO local sin zona) | `201 Created` (`ReservaDTO`) · `400 Bad Request` · `404 Not Found` |
| `GET /reservas/mis-reservas` | `USUARIO` / `ADMIN` | — | `200 OK` (`ReservaDTO[]` del usuario autenticado) |
| `PATCH /reservas/{id}/cancelar` | Propietario (`USUARIO` / `ADMIN`) | `id` (path) | `200 OK` (`ReservaDTO` con `estadoReserva: "CANCELADA"`) · `400` si faltan ≤ 24 h o ya finalizada/cancelada · `403` si pertenece a otro usuario |

### Formato Estándar de Error

Todas las respuestas de error (`400`, `401`, `403`, `404`, `500`) retornan:

```json
{
  "timestamp": "2026-11-10T18:30:00",
  "status": 400,
  "error": "Error de validación en campos",
  "message": "Existen errores de validación en los datos enviados",
  "details": {
    "correo": "El formato del correo electrónico no es válido"
  }
}
```

---

## 7. Ejecución de Pruebas Automatizadas

```bash
./gradlew clean test
```

Incluye pruebas unitarias de todos los servicios y mappers, pruebas de integración de CORS (`CorsIntegrationTest`), sembrado de datos demo (`DemoDataInitializerTest`) y matriz completa de seguridad rol × endpoint (`SecurityHardeningAndMatrixTest`).
