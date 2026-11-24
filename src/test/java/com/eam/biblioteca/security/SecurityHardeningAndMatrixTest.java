package com.eam.biblioteca.security;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import com.eam.biblioteca.persistenceLayer.repository.ReservaRepository;
import com.eam.biblioteca.persistenceLayer.repository.UsuarioRepository;
import com.eam.biblioteca.presentationLayer.exception.GlobalExceptionHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityHardeningAndMatrixTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private String tokenUsuario;
    private String tokenAdmin;
    private UsuarioEntity usuarioRegular;
    private UsuarioEntity usuarioAdmin;
    private GeneroEntity generoBase;
    private AutorEntity autorBase;
    private LibroEntity libroHabilitado;
    private LibroEntity libroInhabilitado;
    private ReservaEntity reservaActiva;

    @BeforeEach
    void setUp() {
        usuarioRegular = usuarioRepository.findByCorreo("lector.matrix@biblioteca.com")
                .orElseGet(() -> usuarioRepository.save(new UsuarioEntity(
                        null, "1094900111", "Lector Matrix", "lector.matrix@biblioteca.com",
                        passwordEncoder.encode("Password123*"), "USUARIO", true
                )));

        usuarioAdmin = usuarioRepository.findByCorreo("admin@biblioteca.com")
                .orElseGet(() -> usuarioRepository.save(new UsuarioEntity(
                        null, "0000000000", "Administrador Principal", "admin@biblioteca.com",
                        passwordEncoder.encode("Admin12345*"), "ADMIN", true
                )));

        tokenUsuario = jwtService.generarToken(usuarioRegular.getCorreo(), "USUARIO");
        tokenAdmin = jwtService.generarToken(usuarioAdmin.getCorreo(), "ADMIN");

        generoBase = generoRepository.save(new GeneroEntity(null, "Ficción Matrix"));

        autorBase = autorRepository.save(new AutorEntity(null, "Autor Matrix"));

        Set<AutorEntity> autores = new LinkedHashSet<>();
        autores.add(autorBase);

        libroHabilitado = libroRepository.save(new LibroEntity(
                null, "978-958-0001", "Alfa Libro Habilitado", generoBase, autores, 2020, true
        ));
        ejemplarRepository.save(new EjemplarEntity(null, libroHabilitado, EjemplarEntity.DISPONIBLE));
        EjemplarEntity ejemplarReservado = ejemplarRepository.save(new EjemplarEntity(null, libroHabilitado, EjemplarEntity.DISPONIBLE));

        libroInhabilitado = libroRepository.save(new LibroEntity(
                null, "978-958-0002", "Beta Libro Inhabilitado", generoBase, new LinkedHashSet<>(autores), 2018, false
        ));

        reservaActiva = reservaRepository.save(new ReservaEntity(
                null,
                usuarioRegular,
                ejemplarReservado,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(7),
                "ACTIVA"
        ));
    }

    @Test
    @DisplayName("B-2: GET /api/v1/catalogo es público, excluye inhabilitados, ordena por título y expone disponibilidad")
    void catalogoPublico_SinToken_RetornaHabilitadosOrdenadosConDisponibilidad() throws Exception {
        mockMvc.perform(get("/api/v1/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[?(@.idLibro == " + libroHabilitado.getIdLibro() + ")].titulo").value(hasItem("Alfa Libro Habilitado")))
                .andExpect(jsonPath("$[?(@.idLibro == " + libroHabilitado.getIdLibro() + ")].anioPublicacion").value(hasItem(2020)))
                .andExpect(jsonPath("$[?(@.idLibro == " + libroHabilitado.getIdLibro() + ")].totalEjemplares").value(hasItem(2)))
                .andExpect(jsonPath("$[?(@.idLibro == " + libroHabilitado.getIdLibro() + ")].ejemplaresDisponibles").value(hasItem(2)))
                .andExpect(jsonPath("$[?(@.idLibro == " + libroInhabilitado.getIdLibro() + ")]").isEmpty());
    }

    @Test
    @DisplayName("B-2: GET /api/v1/catalogo/{id} retorna 200 para habilitado y 404 para inhabilitado o inexistente")
    void catalogoDetallePublico_VerificaVisibilidad() throws Exception {
        mockMvc.perform(get("/api/v1/catalogo/" + libroHabilitado.getIdLibro()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(libroHabilitado.getIdLibro()))
                .andExpect(jsonPath("$.titulo").value("Alfa Libro Habilitado"))
                .andExpect(jsonPath("$.anioPublicacion").value(2020))
                .andExpect(jsonPath("$.totalEjemplares").value(2));

        mockMvc.perform(get("/api/v1/catalogo/" + libroInhabilitado.getIdLibro()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/catalogo/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("B-2: GET /api/v1/libros/{id} se mantiene exclusivo de ADMIN (401 sin token, 403 con USUARIO, 200 con ADMIN)")
    void librosPorId_MantieneRestriccionSoloAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/libros/" + libroHabilitado.getIdLibro()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/libros/" + libroHabilitado.getIdLibro())
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/libros/" + libroHabilitado.getIdLibro())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(libroHabilitado.getIdLibro()));
    }

    @Test
    @DisplayName("B-3: POST /api/v1/libros valida rango @Min(1) @Max(2100) en anioPublicacion")
    void crearLibro_AnioPublicacionFueraDeRango_Retorna400() throws Exception {
        String bodyInvalido = """
                {
                  "isbn": "978-958-9999",
                  "titulo": "Libro Futuro Lejano",
                  "idGenero": %d,
                  "idsAutores": [%d],
                  "anioPublicacion": 2500
                }
                """.formatted(generoBase.getIdGenero(), autorBase.getIdAutor());

        mockMvc.perform(post("/api/v1/libros")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.anioPublicacion").exists());
    }

    @Test
    @DisplayName("B-6(a): GlobalExceptionHandler en error 500 devuelve mensaje genérico y no filtra detalles internos")
    void globalExceptionHandler_Error500_NoFiltraDetalleInterno() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        RuntimeException internalEx = new RuntimeException("SELECT * FROM usuarios WHERE secret_column = 'stack_trace_leak'");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneralException(internalEx);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Error interno del servidor", response.getBody().get("error"));
        assertEquals("Error interno del servidor", response.getBody().get("message"));
        assertFalse(response.getBody().toString().contains("secret_column"));
    }

    @Test
    @DisplayName("B-6(b) y (d): POST /api/v1/auth/register con rol=ADMIN fuerza rol=USUARIO y no expone password")
    void register_ConRolAdmin_FuerzaRolUsuarioYNoExponePassword() throws Exception {
        String body = """
                {
                  "documentoIdentidad": "1094888777",
                  "nombreCompleto": "Intento Escalada",
                  "correo": "escalada@biblioteca.com",
                  "password": "Password123*",
                  "rol": "ADMIN"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("USUARIO"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("B-6(c): Token manipulado, expirado o de usuario deshabilitado recibe 401")
    void jwtFilter_TokenManipuladoExpiradoODeshabilitado_Retorna401() throws Exception {
        // 1. Token manipulado en la firma
        String tokenManipulado = tokenUsuario.substring(0, tokenUsuario.length() - 4) + "XXXX";
        mockMvc.perform(get("/api/v1/generos")
                        .header("Authorization", "Bearer " + tokenManipulado))
                .andExpect(status().isUnauthorized());

        // 2. Token expirado
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String tokenExpirado = Jwts.builder()
                .subject(usuarioRegular.getCorreo())
                .claim("rol", "USUARIO")
                .issuedAt(new Date(System.currentTimeMillis() - 20_000))
                .expiration(new Date(System.currentTimeMillis() - 10_000))
                .signWith(key)
                .compact();

        mockMvc.perform(get("/api/v1/generos")
                        .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());

        // 3. Usuario deshabilitado después de emitir el token
        UsuarioEntity usuarioDeshabilitado = usuarioRepository.save(new UsuarioEntity(
                null, "1094777666", "Usuario Inactivo", "inactivo@biblioteca.com",
                passwordEncoder.encode("Password123*"), "USUARIO", false
        ));
        String tokenInactivo = jwtService.generarToken(usuarioDeshabilitado.getCorreo(), "USUARIO");

        mockMvc.perform(get("/api/v1/generos")
                        .header("Authorization", "Bearer " + tokenInactivo))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("B-7: Ningún endpoint de /api/v1/** queda fuera de la matriz de seguridad")
    void matrizSeguridad_CubreTodosLosEndpointsDeControladores() {
        Set<String> endpointsConocidos = Set.of(
                "POST /api/v1/auth/register",
                "POST /api/v1/auth/login",
                "GET /api/v1/catalogo",
                "GET /api/v1/catalogo/{id}",
                "POST /api/v1/autores",
                "GET /api/v1/autores",
                "POST /api/v1/generos",
                "GET /api/v1/generos",
                "POST /api/v1/libros",
                "DELETE /api/v1/libros/{id}",
                "GET /api/v1/libros/{id}",
                "GET /api/v1/libros/buscar/titulo",
                "GET /api/v1/libros/buscar/genero",
                "GET /api/v1/libros/buscar/autor",
                "PUT /api/v1/libros/{id}",
                "GET /api/v1/libros/{id}/disponibilidad",
                "GET /api/v1/libros/{id}/detalle",
                "POST /api/v1/libros/{idLibro}/ejemplares",
                "GET /api/v1/libros/{idLibro}/ejemplares",
                "POST /api/v1/reservas",
                "GET /api/v1/reservas/mis-reservas",
                "GET /api/v1/reservas/{id}",
                "GET /api/v1/reservas",
                "GET /api/v1/reservas/usuario/{idUsuario}",
                "PATCH /api/v1/reservas/{id}/cancelar",
                "PATCH /api/v1/reservas/{id}/cancelar-admin",
                "POST /api/v1/usuarios",
                "GET /api/v1/usuarios/{id}",
                "GET /api/v1/usuarios/documento/{documento}",
                "PATCH /api/v1/usuarios/documento/{documento}/deshabilitar"
        );

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            Set<String> patterns = info.getPatternValues();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            for (String pattern : patterns) {
                if (pattern.startsWith("/api/v1")) {
                    for (RequestMethod method : methods) {
                        String signature = method.name() + " " + pattern;
                        assertTrue(endpointsConocidos.contains(signature),
                                "Endpoint fuera de la matriz de pruebas de seguridad: " + signature);
                    }
                }
            }
        }
    }

    static Stream<Arguments> matrizAutorizacionEndpoints() {
        // Argumentos: HttpMethod, pathTemplate, rol ("ANON", "USUARIO", "ADMIN"), statusEsperado
        return Stream.of(
                // Públicos
                Arguments.of(HttpMethod.GET, "/api/v1/catalogo", "ANON", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/catalogo/{idLibro}", "ANON", 200),

                // Autores y Géneros
                Arguments.of(HttpMethod.GET, "/api/v1/autores", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/autores", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/autores", "ADMIN", 200),
                Arguments.of(HttpMethod.POST, "/api/v1/autores", "ANON", 401),
                Arguments.of(HttpMethod.POST, "/api/v1/autores", "USUARIO", 403),

                Arguments.of(HttpMethod.GET, "/api/v1/generos", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/generos", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/generos", "ADMIN", 200),
                Arguments.of(HttpMethod.POST, "/api/v1/generos", "ANON", 401),
                Arguments.of(HttpMethod.POST, "/api/v1/generos", "USUARIO", 403),

                // Libros y Ejemplares
                Arguments.of(HttpMethod.POST, "/api/v1/libros", "ANON", 401),
                Arguments.of(HttpMethod.POST, "/api/v1/libros", "USUARIO", 403),
                Arguments.of(HttpMethod.PUT, "/api/v1/libros/{idLibro}", "ANON", 401),
                Arguments.of(HttpMethod.PUT, "/api/v1/libros/{idLibro}", "USUARIO", 403),
                Arguments.of(HttpMethod.DELETE, "/api/v1/libros/{idLibro}", "ANON", 401),
                Arguments.of(HttpMethod.DELETE, "/api/v1/libros/{idLibro}", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}", "ADMIN", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/detalle", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/detalle", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/detalle", "ADMIN", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/ejemplares", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/ejemplares", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/ejemplares", "ADMIN", 200),
                Arguments.of(HttpMethod.POST, "/api/v1/libros/{idLibro}/ejemplares", "ANON", 401),
                Arguments.of(HttpMethod.POST, "/api/v1/libros/{idLibro}/ejemplares", "USUARIO", 403),

                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/titulo?titulo=Alfa", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/titulo?titulo=Alfa", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/genero?genero=Ficción", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/genero?genero=Ficción", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/autor?autor=Autor", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/buscar/autor?autor=Autor", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/disponibilidad?fechaHora=2026-12-01T10:00:00", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/libros/{idLibro}/disponibilidad?fechaHora=2026-12-01T10:00:00", "USUARIO", 200),

                // Reservas
                Arguments.of(HttpMethod.POST, "/api/v1/reservas", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/mis-reservas", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/mis-reservas", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/{idReserva}", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/{idReserva}", "USUARIO", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/{idReserva}", "ADMIN", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas", "ADMIN", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/usuario/{idUsuario}", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/usuario/{idUsuario}", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/reservas/usuario/{idUsuario}", "ADMIN", 200),
                Arguments.of(HttpMethod.PATCH, "/api/v1/reservas/{idReserva}/cancelar", "ANON", 401),
                Arguments.of(HttpMethod.PATCH, "/api/v1/reservas/{idReserva}/cancelar-admin", "ANON", 401),
                Arguments.of(HttpMethod.PATCH, "/api/v1/reservas/{idReserva}/cancelar-admin", "USUARIO", 403),

                // Usuarios (Solo ADMIN)
                Arguments.of(HttpMethod.POST, "/api/v1/usuarios", "ANON", 401),
                Arguments.of(HttpMethod.POST, "/api/v1/usuarios", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/{idUsuario}", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/{idUsuario}", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/{idUsuario}", "ADMIN", 200),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/documento/{documento}", "ANON", 401),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/documento/{documento}", "USUARIO", 403),
                Arguments.of(HttpMethod.GET, "/api/v1/usuarios/documento/{documento}", "ADMIN", 200),
                Arguments.of(HttpMethod.PATCH, "/api/v1/usuarios/documento/{documento}/deshabilitar", "ANON", 401),
                Arguments.of(HttpMethod.PATCH, "/api/v1/usuarios/documento/{documento}/deshabilitar", "USUARIO", 403)
        );
    }

    @ParameterizedTest(name = "{0} {1} con rol {2} -> {3}")
    @MethodSource("matrizAutorizacionEndpoints")
    @DisplayName("B-7: Verificación parametrizada de matriz rol × endpoint")
    void verificarMatrizAutorizacion(HttpMethod method, String pathTemplate, String rol, int statusEsperado) throws Exception {
        String resolvedPath = pathTemplate
                .replace("{idLibro}", String.valueOf(libroHabilitado.getIdLibro()))
                .replace("{idReserva}", String.valueOf(reservaActiva.getIdReserva()))
                .replace("{idUsuario}", String.valueOf(usuarioRegular.getIdUsuario()))
                .replace("{documento}", usuarioRegular.getDocumentoIdentidad());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, resolvedPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        if ("USUARIO".equals(rol)) {
            builder.header("Authorization", "Bearer " + tokenUsuario);
        } else if ("ADMIN".equals(rol)) {
            builder.header("Authorization", "Bearer " + tokenAdmin);
        }

        mockMvc.perform(builder)
                .andExpect(status().is(statusEsperado));
    }
}
