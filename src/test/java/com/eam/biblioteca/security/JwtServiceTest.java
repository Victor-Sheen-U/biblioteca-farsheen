package com.eam.biblioteca.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "clave-super-secreta-de-prueba-de-al-menos-32-bytes-123456";
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRETO, 60000L);
    }

    @Test
    @DisplayName("generarToken genera un token no nulo y no vacío")
    void generarToken_GeneraTokenNoVacio() {
        String token = jwtService.generarToken("usuario@biblioteca.com", "USUARIO");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("extraerCorreo retorna el correo usado como subject")
    void extraerCorreo_RetornaCorreoCorrecto() {
        String correo = "admin@biblioteca.com";
        String token = jwtService.generarToken(correo, "ADMIN");
        String extraido = jwtService.extraerCorreo(token);
        assertEquals(correo, extraido);
    }

    @Test
    @DisplayName("esTokenValido retorna true para un token recién generado")
    void esTokenValido_TokenValido_RetornaTrue() {
        String token = jwtService.generarToken("test@biblioteca.com", "USUARIO");
        assertTrue(jwtService.esTokenValido(token));
    }

    @Test
    @DisplayName("esTokenValido retorna false para un token expirado")
    void esTokenValido_TokenExpirado_RetornaFalse() {
        JwtService servicioExpirado = new JwtService(SECRETO, -1000L);
        String token = servicioExpirado.generarToken("expirado@biblioteca.com", "USUARIO");
        assertFalse(jwtService.esTokenValido(token));
    }

    @Test
    @DisplayName("esTokenValido retorna false para un token firmado con otro secreto")
    void esTokenValido_FirmaOtroSecreto_RetornaFalse() {
        String otroSecreto = "otro-secreto-completamente-distinto-de-32-bytes-987654";
        JwtService otroServicio = new JwtService(otroSecreto, 60000L);
        String token = otroServicio.generarToken("test@biblioteca.com", "USUARIO");

        assertFalse(jwtService.esTokenValido(token));
    }

    @Test
    @DisplayName("esTokenValido retorna false para un token malformado o vacío")
    void esTokenValido_TokenMalformado_RetornaFalse() {
        assertFalse(jwtService.esTokenValido("token.invalido.malformado"));
        assertFalse(jwtService.esTokenValido(""));
        assertFalse(jwtService.esTokenValido(null));
    }
}
