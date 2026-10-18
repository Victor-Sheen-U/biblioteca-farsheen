package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioDao usuarioDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("registrar guarda usuario con rol USUARIO, estado true, correo normalizado y password hasheada")
    void registrar_DatosValidos_GuardaUsuarioCorrectamente() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("12345678", "Carlos Gómez", "  Carlos@CORREO.COM  ", "ClaveSegura123", null);
        when(usuarioDao.existsByDocumento("12345678")).thenReturn(false);
        when(usuarioDao.existsByCorreo("carlos@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("$2a$10$hashedPassword");

        UsuarioDTO expectedDTO = new UsuarioDTO(1L, "12345678", "Carlos Gómez", "carlos@correo.com", "USUARIO", true);
        when(usuarioDao.save(any(UsuarioEntity.class))).thenReturn(expectedDTO);

        // Act
        UsuarioDTO result = usuarioService.registrar(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioDao).save(captor.capture());
        UsuarioEntity captured = captor.getValue();

        assertEquals("12345678", captured.getDocumentoIdentidad());
        assertEquals("Carlos Gómez", captured.getNombreCompleto());
        assertEquals("carlos@correo.com", captured.getCorreo());
        assertEquals("$2a$10$hashedPassword", captured.getPassword());
        assertEquals(UsuarioEntity.ROL_USUARIO, captured.getRol());
        assertTrue(captured.getEstado());
    }

    @Test
    @DisplayName("registrar con documento duplicado lanza BadRequestException y nunca llama a save")
    void registrar_DocumentoDuplicado_LanzaBadRequestException() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("12345678", "Carlos Gómez", "carlos@correo.com", "ClaveSegura123", null);
        when(usuarioDao.existsByDocumento("12345678")).thenReturn(true);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> usuarioService.registrar(dto));
        assertTrue(ex.getMessage().contains("12345678"));
        verify(usuarioDao, never()).save(any());
    }

    @Test
    @DisplayName("registrar con correo duplicado lanza BadRequestException y nunca llama a save")
    void registrar_CorreoDuplicado_LanzaBadRequestException() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("12345678", "Carlos Gómez", "carlos@correo.com", "ClaveSegura123", null);
        when(usuarioDao.existsByDocumento("12345678")).thenReturn(false);
        when(usuarioDao.existsByCorreo("carlos@correo.com")).thenReturn(true);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> usuarioService.registrar(dto));
        assertTrue(ex.getMessage().contains("carlos@correo.com"));
        verify(usuarioDao, never()).save(any());
    }

    @Test
    @DisplayName("crearComoAdmin con rol ADMIN guarda rol ADMIN")
    void crearComoAdmin_ConRolAdmin_GuardaRolAdmin() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("87654321", "Admin User", "admin@correo.com", "ClaveSegura123", "ADMIN");
        when(usuarioDao.existsByDocumento("87654321")).thenReturn(false);
        when(usuarioDao.existsByCorreo("admin@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("$2a$10$hashedAdmin");

        UsuarioDTO expectedDTO = new UsuarioDTO(2L, "87654321", "Admin User", "admin@correo.com", "ADMIN", true);
        when(usuarioDao.save(any(UsuarioEntity.class))).thenReturn(expectedDTO);

        // Act
        UsuarioDTO result = usuarioService.crearComoAdmin(dto);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getRol());

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioDao).save(captor.capture());
        assertEquals(UsuarioEntity.ROL_ADMIN, captor.getValue().getRol());
    }

    @Test
    @DisplayName("crearComoAdmin sin rol asigna por defecto USUARIO")
    void crearComoAdmin_SinRol_AsignaRolUsuario() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("11223344", "User Normal", "normal@correo.com", "ClaveSegura123", null);
        when(usuarioDao.existsByDocumento("11223344")).thenReturn(false);
        when(usuarioDao.existsByCorreo("normal@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("$2a$10$hashedNormal");

        UsuarioDTO expectedDTO = new UsuarioDTO(3L, "11223344", "User Normal", "normal@correo.com", "USUARIO", true);
        when(usuarioDao.save(any(UsuarioEntity.class))).thenReturn(expectedDTO);

        // Act
        UsuarioDTO result = usuarioService.crearComoAdmin(dto);

        // Assert
        assertNotNull(result);
        assertEquals("USUARIO", result.getRol());

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioDao).save(captor.capture());
        assertEquals(UsuarioEntity.ROL_USUARIO, captor.getValue().getRol());
    }

    @Test
    @DisplayName("crearComoAdmin con rol inválido lanza BadRequestException")
    void crearComoAdmin_RolInvalido_LanzaBadRequestException() {
        // Arrange
        UsuarioCreateDTO dto = new UsuarioCreateDTO("11223344", "User Error", "error@correo.com", "ClaveSegura123", "SUPERADMIN");
        when(usuarioDao.existsByDocumento("11223344")).thenReturn(false);
        when(usuarioDao.existsByCorreo("error@correo.com")).thenReturn(false);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> usuarioService.crearComoAdmin(dto));
        assertTrue(ex.getMessage().contains("SUPERADMIN"));
        verify(usuarioDao, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId cuando existe retorna UsuarioDTO")
    void buscarPorId_Existe_RetornaUsuarioDTO() {
        // Arrange
        Long id = 1L;
        UsuarioDTO dto = new UsuarioDTO(id, "12345678", "Carlos", "carlos@correo.com", "USUARIO", true);
        when(usuarioDao.findById(id)).thenReturn(Optional.of(dto));

        // Act
        UsuarioDTO result = usuarioService.buscarPorId(id);

        // Assert
        assertEquals(dto, result);
        verify(usuarioDao).findById(id);
    }

    @Test
    @DisplayName("buscarPorId cuando no existe lanza ResourceNotFoundException")
    void buscarPorId_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long id = 99L;
        when(usuarioDao.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(id));
        assertTrue(ex.getMessage().contains("99"));
        verify(usuarioDao).findById(id);
    }

    @Test
    @DisplayName("buscarPorDocumento cuando existe retorna UsuarioDTO")
    void buscarPorDocumento_Existe_RetornaUsuarioDTO() {
        // Arrange
        String doc = "12345678";
        UsuarioDTO dto = new UsuarioDTO(1L, doc, "Carlos", "carlos@correo.com", "USUARIO", true);
        when(usuarioDao.findByDocumento(doc)).thenReturn(Optional.of(dto));

        // Act
        UsuarioDTO result = usuarioService.buscarPorDocumento(doc);

        // Assert
        assertEquals(dto, result);
        verify(usuarioDao).findByDocumento(doc);
    }

    @Test
    @DisplayName("buscarPorDocumento cuando no existe lanza ResourceNotFoundException")
    void buscarPorDocumento_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        String doc = "99999999";
        when(usuarioDao.findByDocumento(doc)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorDocumento(doc));
        assertTrue(ex.getMessage().contains(doc));
        verify(usuarioDao).findByDocumento(doc);
    }

    @Test
    @DisplayName("deshabilitarPorDocumento cuando existe cambia estado a false y guarda")
    void deshabilitarPorDocumento_Existe_CambiaEstadoAFalso() {
        // Arrange
        String doc = "12345678";
        UsuarioEntity entity = new UsuarioEntity(1L, doc, "Carlos", "carlos@correo.com", "pass", "USUARIO", true);
        when(usuarioDao.findEntityByDocumento(doc)).thenReturn(Optional.of(entity));

        UsuarioDTO expectedDTO = new UsuarioDTO(1L, doc, "Carlos", "carlos@correo.com", "USUARIO", false);
        when(usuarioDao.save(entity)).thenReturn(expectedDTO);

        // Act
        UsuarioDTO result = usuarioService.deshabilitarPorDocumento(doc);

        // Assert
        assertNotNull(result);
        assertFalse(result.getEstado());
        assertFalse(entity.getEstado());
        verify(usuarioDao).save(entity);
    }

    @Test
    @DisplayName("deshabilitarPorDocumento cuando no existe lanza ResourceNotFoundException")
    void deshabilitarPorDocumento_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        String doc = "99999999";
        when(usuarioDao.findEntityByDocumento(doc)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> usuarioService.deshabilitarPorDocumento(doc));
        assertTrue(ex.getMessage().contains(doc));
        verify(usuarioDao, never()).save(any());
    }
}
