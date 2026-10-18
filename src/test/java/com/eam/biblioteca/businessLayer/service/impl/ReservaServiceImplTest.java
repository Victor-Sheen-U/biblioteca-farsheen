package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaCreateDTO;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.dao.ReservaDao;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
import com.eam.biblioteca.presentationLayer.exception.ForbiddenException;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaDao reservaDao;

    @Mock
    private UsuarioDao usuarioDao;

    @Mock
    private LibroDao libroDao;

    @Mock
    private EjemplarDao ejemplarDao;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    @DisplayName("crear reserva OK asigna el ejemplar de menor id, estado ACTIVA y usuario del correo")
    void crear_DatosValidos_CreaReservaExitosamente() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime entrega = LocalDateTime.now().plusDays(5);
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, entrega);

        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(1L);
        libro.setTitulo("Libro Test");
        libro.setEstado(true);

        EjemplarEntity e1 = new EjemplarEntity(101L, libro, EjemplarEntity.DISPONIBLE);
        EjemplarEntity e2 = new EjemplarEntity(102L, libro, EjemplarEntity.DISPONIBLE);

        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(usuario));
        when(libroDao.findEntityById(1L)).thenReturn(Optional.of(libro));
        when(ejemplarDao.findDisponiblesEnRango(1L, inicio, entrega)).thenReturn(List.of(e1, e2));

        ReservaDTO expectedDTO = new ReservaDTO(1L, 10L, "Juan", 101L, 1L, "Libro Test", LocalDateTime.now(), inicio, entrega, "ACTIVA");
        when(reservaDao.save(any(ReservaEntity.class))).thenReturn(expectedDTO);

        // Act
        ReservaDTO result = reservaService.crear(correo, dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<ReservaEntity> captor = ArgumentCaptor.forClass(ReservaEntity.class);
        verify(reservaDao).save(captor.capture());
        ReservaEntity captured = captor.getValue();

        assertEquals(usuario, captured.getUsuario());
        assertEquals(101L, captured.getEjemplar().getIdEjemplar());
        assertEquals(ReservaEntity.ACTIVA, captured.getEstadoReserva());
        assertEquals(inicio, captured.getFechaInicio());
        assertEquals(entrega, captured.getFechaEntrega());
    }

    @Test
    @DisplayName("crear reserva sin ejemplares disponibles lanza BadRequestException")
    void crear_SinEjemplaresDisponibles_LanzaBadRequestException() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime entrega = LocalDateTime.now().plusDays(5);
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, entrega);

        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(1L);
        libro.setEstado(true);

        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(usuario));
        when(libroDao.findEntityById(1L)).thenReturn(Optional.of(libro));
        when(ejemplarDao.findDisponiblesEnRango(1L, inicio, entrega)).thenReturn(Collections.emptyList());

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains("No hay ejemplares disponibles"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("crear reserva con libro inexistente lanza ResourceNotFoundException")
    void crear_LibroInexistente_LanzaResourceNotFoundException() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        ReservaCreateDTO dto = new ReservaCreateDTO(99L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5));
        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(new UsuarioEntity()));
        when(libroDao.findEntityById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains("99"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("crear reserva con libro inhabilitado lanza ResourceNotFoundException")
    void crear_LibroInhabilitado_LanzaResourceNotFoundException() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5));
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(1L);
        libro.setEstado(false);

        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(new UsuarioEntity()));
        when(libroDao.findEntityById(1L)).thenReturn(Optional.of(libro));

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains("1"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("crear reserva con usuario inexistente lanza ResourceNotFoundException")
    void crear_UsuarioInexistente_LanzaResourceNotFoundException() {
        // Arrange
        String correo = "noexiste@biblioteca.com";
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5));
        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains(correo));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("crear reserva con fecha de inicio pasada lanza BadRequestException")
    void crear_FechaInicioPasada_LanzaBadRequestException() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime entrega = LocalDateTime.now().plusDays(2);
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, entrega);

        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(1L);
        libro.setEstado(true);

        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(new UsuarioEntity()));
        when(libroDao.findEntityById(1L)).thenReturn(Optional.of(libro));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains("futura"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("crear reserva con entrega anterior o igual al inicio lanza BadRequestException")
    void crear_EntregaAnteriorAInicio_LanzaBadRequestException() {
        // Arrange
        String correo = "usuario@biblioteca.com";
        LocalDateTime inicio = LocalDateTime.now().plusDays(5);
        LocalDateTime entrega = LocalDateTime.now().plusDays(2);
        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, entrega);

        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(1L);
        libro.setEstado(true);

        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(new UsuarioEntity()));
        when(libroDao.findEntityById(1L)).thenReturn(Optional.of(libro));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crear(correo, dto));
        assertTrue(ex.getMessage().contains("posterior"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId dueño de la reserva retorna ReservaDTO")
    void buscarPorId_Duenio_RetornaReservaDTO() {
        // Arrange
        Long id = 1L;
        String correo = "usuario@biblioteca.com";
        ReservaDTO dto = new ReservaDTO(id, 10L, "Juan", 101L, 1L, "Libro", LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), "ACTIVA");
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);

        when(reservaDao.findById(id)).thenReturn(Optional.of(dto));
        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(usuario));

        // Act
        ReservaDTO result = reservaService.buscarPorId(id, correo, false);

        // Assert
        assertEquals(dto, result);
        verify(reservaDao).findById(id);
    }

    @Test
    @DisplayName("buscarPorId otro usuario ajeno lanza ForbiddenException")
    void buscarPorId_UsuarioAjeno_LanzaForbiddenException() {
        // Arrange
        Long id = 1L;
        String correo = "otro@biblioteca.com";
        ReservaDTO dto = new ReservaDTO(id, 10L, "Juan", 101L, 1L, "Libro", LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), "ACTIVA");
        UsuarioEntity otroUsuario = new UsuarioEntity(20L, "999", "Pedro", correo, "pass", "USUARIO", true);

        when(reservaDao.findById(id)).thenReturn(Optional.of(dto));
        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(otroUsuario));

        // Act & Assert
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> reservaService.buscarPorId(id, correo, false));
        assertTrue(ex.getMessage().contains("permiso"));
    }

    @Test
    @DisplayName("buscarPorId como admin retorna reserva aunque no sea el dueño")
    void buscarPorId_Admin_RetornaReserva() {
        // Arrange
        Long id = 1L;
        ReservaDTO dto = new ReservaDTO(id, 10L, "Juan", 101L, 1L, "Libro", LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), "ACTIVA");
        when(reservaDao.findById(id)).thenReturn(Optional.of(dto));

        // Act
        ReservaDTO result = reservaService.buscarPorId(id, "admin@biblioteca.com", true);

        // Assert
        assertEquals(dto, result);
        verify(usuarioDao, never()).findEntityByCorreo(any());
    }

    @Test
    @DisplayName("buscarPorId cuando no existe lanza ResourceNotFoundException")
    void buscarPorId_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long id = 99L;
        when(reservaDao.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.buscarPorId(id, "admin@biblioteca.com", true));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("listarTodas retorna la lista de reservas del DAO")
    void listarTodas_RetornaLista() {
        List<ReservaDTO> list = List.of(new ReservaDTO());
        when(reservaDao.findAll()).thenReturn(list);

        List<ReservaDTO> result = reservaService.listarTodas();
        assertEquals(list, result);
        verify(reservaDao).findAll();
    }

    @Test
    @DisplayName("listarPorUsuario retorna reservas cuando usuario existe")
    void listarPorUsuario_Existe_RetornaLista() {
        Long idUsuario = 10L;
        when(usuarioDao.findEntityById(idUsuario)).thenReturn(Optional.of(new UsuarioEntity()));
        List<ReservaDTO> list = List.of(new ReservaDTO());
        when(reservaDao.findByUsuarioId(idUsuario)).thenReturn(list);

        List<ReservaDTO> result = reservaService.listarPorUsuario(idUsuario);
        assertEquals(list, result);
        verify(reservaDao).findByUsuarioId(idUsuario);
    }

    @Test
    @DisplayName("listarPorUsuario cuando usuario no existe lanza ResourceNotFoundException")
    void listarPorUsuario_NoExiste_LanzaResourceNotFoundException() {
        Long idUsuario = 99L;
        when(usuarioDao.findEntityById(idUsuario)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.listarPorUsuario(idUsuario));
        assertTrue(ex.getMessage().contains("99"));
        verify(reservaDao, never()).findByUsuarioId(any());
    }

    @Test
    @DisplayName("listarMisReservas retorna las reservas del usuario autenticado")
    void listarMisReservas_RetornaLista() {
        String correo = "usuario@biblioteca.com";
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);
        when(usuarioDao.findEntityByCorreo(correo)).thenReturn(Optional.of(usuario));
        List<ReservaDTO> list = List.of(new ReservaDTO());
        when(reservaDao.findByUsuarioId(10L)).thenReturn(list);

        List<ReservaDTO> result = reservaService.listarMisReservas(correo);
        assertEquals(list, result);
        verify(reservaDao).findByUsuarioId(10L);
    }

    @Test
    @DisplayName("cancelarComoUsuario con inicio > 24h cambia estado a CANCELADA")
    void cancelarComoUsuario_ConAntelacionMayor24h_CancelaExitosamente() {
        // Arrange
        Long id = 1L;
        String correo = "usuario@biblioteca.com";
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaInicio(LocalDateTime.now().plusDays(3)); // 72h antes
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(5));

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        ReservaDTO expectedDTO = new ReservaDTO(id, 10L, "Juan", 101L, 1L, "Libro", LocalDateTime.now(), reserva.getFechaInicio(), reserva.getFechaEntrega(), "CANCELADA");
        when(reservaDao.save(reserva)).thenReturn(expectedDTO);

        // Act
        ReservaDTO result = reservaService.cancelarComoUsuario(id, correo);

        // Assert
        assertNotNull(result);
        assertEquals(ReservaEntity.CANCELADA, reserva.getEstadoReserva());
        verify(reservaDao).save(reserva);
    }

    @Test
    @DisplayName("cancelarComoUsuario con inicio en menos de 24h lanza BadRequestException")
    void cancelarComoUsuario_MenosDe24h_LanzaBadRequestException() {
        // Arrange
        Long id = 1L;
        String correo = "usuario@biblioteca.com";
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaInicio(LocalDateTime.now().plusHours(10)); // < 24h
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(2));

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.cancelarComoUsuario(id, correo));
        assertTrue(ex.getMessage().contains("24 horas"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoUsuario reserva ajena lanza ForbiddenException")
    void cancelarComoUsuario_ReservaAjena_LanzaForbiddenException() {
        // Arrange
        Long id = 1L;
        String correo = "intruso@biblioteca.com";
        UsuarioEntity duenio = new UsuarioEntity(10L, "123", "Juan", "duenio@biblioteca.com", "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(duenio);

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        // Act & Assert
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> reservaService.cancelarComoUsuario(id, correo));
        assertTrue(ex.getMessage().contains("no le pertenece"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoUsuario reserva ya cancelada lanza BadRequestException")
    void cancelarComoUsuario_YaCancelada_LanzaBadRequestException() {
        // Arrange
        Long id = 1L;
        String correo = "usuario@biblioteca.com";
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(ReservaEntity.CANCELADA);
        reserva.setFechaInicio(LocalDateTime.now().plusDays(5));
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(7));

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.cancelarComoUsuario(id, correo));
        assertTrue(ex.getMessage().contains("ya está cancelada"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoUsuario reserva finalizada lanza BadRequestException")
    void cancelarComoUsuario_Finalizada_LanzaBadRequestException() {
        // Arrange
        Long id = 1L;
        String correo = "usuario@biblioteca.com";
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", correo, "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaInicio(LocalDateTime.now().minusDays(5));
        reserva.setFechaEntrega(LocalDateTime.now().minusDays(2)); // finalizada por fecha

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.cancelarComoUsuario(id, correo));
        assertTrue(ex.getMessage().contains("finalizada"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoUsuario reserva inexistente lanza ResourceNotFoundException")
    void cancelarComoUsuario_Inexistente_LanzaResourceNotFoundException() {
        Long id = 99L;
        when(reservaDao.findEntityById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelarComoUsuario(id, "correo@test.com"));
        assertTrue(ex.getMessage().contains("99"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoAdmin cancela exitosamente incluso con inicio en menos de 24h")
    void cancelarComoAdmin_MenosDe24h_CancelaExitosamente() {
        // Arrange
        Long id = 1L;
        UsuarioEntity usuario = new UsuarioEntity(10L, "123", "Juan", "user@test.com", "pass", "USUARIO", true);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaInicio(LocalDateTime.now().plusHours(2)); // < 24h
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(2));

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        ReservaDTO expectedDTO = new ReservaDTO(id, 10L, "Juan", 101L, 1L, "Libro", LocalDateTime.now(), reserva.getFechaInicio(), reserva.getFechaEntrega(), "CANCELADA");
        when(reservaDao.save(reserva)).thenReturn(expectedDTO);

        // Act
        ReservaDTO result = reservaService.cancelarComoAdmin(id);

        // Assert
        assertNotNull(result);
        assertEquals(ReservaEntity.CANCELADA, reserva.getEstadoReserva());
        verify(reservaDao).save(reserva);
    }

    @Test
    @DisplayName("cancelarComoAdmin reserva ya cancelada lanza BadRequestException")
    void cancelarComoAdmin_YaCancelada_LanzaBadRequestException() {
        Long id = 1L;
        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(id);
        reserva.setEstadoReserva(ReservaEntity.CANCELADA);

        when(reservaDao.findEntityById(id)).thenReturn(Optional.of(reserva));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.cancelarComoAdmin(id));
        assertTrue(ex.getMessage().contains("ya está cancelada"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    @DisplayName("cancelarComoAdmin reserva inexistente lanza ResourceNotFoundException")
    void cancelarComoAdmin_Inexistente_LanzaResourceNotFoundException() {
        Long id = 99L;
        when(reservaDao.findEntityById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelarComoAdmin(id));
        assertTrue(ex.getMessage().contains("99"));
        verify(reservaDao, never()).save(any());
    }
}
