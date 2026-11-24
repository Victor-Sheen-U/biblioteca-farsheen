package com.eam.biblioteca.security;

import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import com.eam.biblioteca.persistenceLayer.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityRulesTest {

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
    private PasswordEncoder passwordEncoder;

    private UsuarioEntity usuarioActivo;
    private UsuarioEntity usuarioDeshabilitado;
    private LibroEntity libroActivo;
    private String tokenUsuario;
    private String tokenDeshabilitado;

    @BeforeEach
    void setUp() {
        usuarioActivo = usuarioRepository.save(new UsuarioEntity(
                null, "1094001001", "Lector Activo", "lector.rules@biblioteca.com",
                passwordEncoder.encode("Password123*"), "USUARIO", true
        ));
        usuarioDeshabilitado = usuarioRepository.save(new UsuarioEntity(
                null, "1094001002", "Lector Inactivo", "inactivo.rules@biblioteca.com",
                passwordEncoder.encode("Password123*"), "USUARIO", false
        ));

        GeneroEntity genero = generoRepository.save(new GeneroEntity(null, "Novela Rules"));
        AutorEntity autor = autorRepository.save(new AutorEntity(null, "Autor Rules"));
        libroActivo = libroRepository.save(new LibroEntity(
                null, "978-0132350884", "Libro Rules", genero, new LinkedHashSet<>(Set.of(autor)), 2020, true
        ));
        ejemplarRepository.save(new EjemplarEntity(null, libroActivo, EjemplarEntity.DISPONIBLE));

        tokenUsuario = jwtService.generarToken(usuarioActivo.getCorreo(), "USUARIO");
        tokenDeshabilitado = jwtService.generarToken(usuarioDeshabilitado.getCorreo(), "USUARIO");
    }

    @Test
    @DisplayName("Catálogo público GET /api/v1/catalogo responde 200 sin token")
    void catalogoPublico_SinToken_Retorna200() throws Exception {
        mockMvc.perform(get("/api/v1/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/libros/1 sin token responde 401")
    void libroPorId_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/libros/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Token alterado responde 401")
    void endpointProtegido_TokenAlterado_Retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/libros/" + libroActivo.getIdLibro())
                        .header("Authorization", "Bearer " + tokenUsuario + "alterado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Rol USUARIO en /api/v1/usuarios/{id} responde 403")
    void usuariosPorId_RolUsuario_Retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/" + usuarioActivo.getIdUsuario())
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Usuario deshabilitado con token válido responde 401")
    void endpointProtegido_UsuarioDeshabilitadoConTokenValido_Retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/generos")
                        .header("Authorization", "Bearer " + tokenDeshabilitado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/catalogo no está permitido sin token (401)")
    void postCatalogo_SinToken_NoPermitido() throws Exception {
        mockMvc.perform(post("/api/v1/catalogo"))
                .andExpect(status().isUnauthorized());
    }
}
