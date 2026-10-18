package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.auth.AuthResponseDTO;
import com.eam.biblioteca.businessLayer.dto.auth.LoginRequestDTO;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.UnauthorizedException;
import com.eam.biblioteca.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioDao usuarioDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("login exitoso retorna AuthResponseDTO con token y datos del usuario")
    void login_CredencialesCorrectas_RetornaAuthResponseDTO() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("  Usuario@Biblioteca.com ", "MiPassword123");
        UsuarioEntity usuario = new UsuarioEntity(1L, "12345678", "Usuario Test", "usuario@biblioteca.com", "$2a$10$hashed", "USUARIO", true);

        when(usuarioDao.findEntityByCorreo("usuario@biblioteca.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("MiPassword123", "$2a$10$hashed")).thenReturn(true);
        when(jwtService.generarToken("usuario@biblioteca.com", "USUARIO")).thenReturn("token.jwt.valido");
        when(jwtService.getExpirationMs()).thenReturn(7200000L);

        // Act
        AuthResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token.jwt.valido", response.getToken());
        assertEquals("Bearer", response.getTipoToken());
        assertEquals(7200000L, response.getExpiraEnMs());
        assertEquals("usuario@biblioteca.com", response.getCorreo());
        assertEquals("USUARIO", response.getRol());

        verify(usuarioDao).findEntityByCorreo("usuario@biblioteca.com");
        verify(passwordEncoder).matches("MiPassword123", "$2a$10$hashed");
        verify(jwtService).generarToken("usuario@biblioteca.com", "USUARIO");
    }

    @Test
    @DisplayName("login con correo inexistente lanza UnauthorizedException con mensaje 'Credenciales inválidas'")
    void login_CorreoInexistente_LanzaUnauthorizedException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("noexiste@biblioteca.com", "MiPassword123");
        when(usuarioDao.findEntityByCorreo("noexiste@biblioteca.com")).thenReturn(Optional.empty());

        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generarToken(any(), any());
    }

    @Test
    @DisplayName("login con contraseña incorrecta lanza UnauthorizedException con mensaje 'Credenciales inválidas'")
    void login_PasswordIncorrecta_LanzaUnauthorizedException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("usuario@biblioteca.com", "PasswordErronea");
        UsuarioEntity usuario = new UsuarioEntity(1L, "12345678", "Usuario Test", "usuario@biblioteca.com", "$2a$10$hashed", "USUARIO", true);

        when(usuarioDao.findEntityByCorreo("usuario@biblioteca.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("PasswordErronea", "$2a$10$hashed")).thenReturn(false);

        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(jwtService, never()).generarToken(any(), any());
    }

    @Test
    @DisplayName("login con usuario deshabilitado lanza UnauthorizedException con mensaje 'Credenciales inválidas'")
    void login_UsuarioDeshabilitado_LanzaUnauthorizedException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("deshabilitado@biblioteca.com", "MiPassword123");
        UsuarioEntity usuario = new UsuarioEntity(1L, "12345678", "Usuario Test", "deshabilitado@biblioteca.com", "$2a$10$hashed", "USUARIO", false);

        when(usuarioDao.findEntityByCorreo("deshabilitado@biblioteca.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("MiPassword123", "$2a$10$hashed")).thenReturn(true);

        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(jwtService, never()).generarToken(any(), any());
    }
}
