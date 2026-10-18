package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.businessLayer.dto.libro.*;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.persistenceLayer.dao.AutorDao;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.GeneroDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.dao.ReservaDao;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroDao libroDao;

    @Mock
    private GeneroDao generoDao;

    @Mock
    private AutorDao autorDao;

    @Mock
    private EjemplarDao ejemplarDao;

    @Mock
    private ReservaDao reservaDao;

    @InjectMocks
    private LibroServiceImpl libroService;

    @Test
    @DisplayName("crear libro exitoso con estado true, género y autores asignados")
    void crear_DatosValidos_GuardaLibroCorrectamente() {
        // Arrange
        LibroCreateDTO dto = new LibroCreateDTO("978-012345", "Rayuela", 1L, List.of(10L));
        GeneroEntity genero = new GeneroEntity(1L, "Novela");
        AutorEntity autor = new AutorEntity(10L, "Julio Cortázar");

        when(generoDao.findEntityById(1L)).thenReturn(Optional.of(genero));
        when(autorDao.findEntitiesByIds(List.of(10L))).thenReturn(List.of(autor));

        LibroDTO expectedDTO = new LibroDTO(1L, "978-012345", "Rayuela", 1L, "Novela",
                List.of(new AutorDTO(10L, "Julio Cortázar")), true);
        when(libroDao.save(any(LibroEntity.class))).thenReturn(expectedDTO);

        // Act
        LibroDTO result = libroService.crear(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<LibroEntity> captor = ArgumentCaptor.forClass(LibroEntity.class);
        verify(libroDao).save(captor.capture());
        LibroEntity captured = captor.getValue();

        assertEquals("978-012345", captured.getIsbn());
        assertEquals("Rayuela", captured.getTitulo());
        assertEquals(genero, captured.getGenero());
        assertTrue(captured.getAutores().contains(autor));
        assertTrue(captured.getEstado());
    }

    @Test
    @DisplayName("crear con género inexistente lanza ResourceNotFoundException")
    void crear_GeneroInexistente_LanzaResourceNotFoundException() {
        // Arrange
        LibroCreateDTO dto = new LibroCreateDTO("978-012345", "Rayuela", 99L, List.of(10L));
        when(generoDao.findEntityById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.crear(dto));
        assertTrue(ex.getMessage().contains("99"));
        verify(autorDao, never()).findEntitiesByIds(any());
        verify(libroDao, never()).save(any());
    }

    @Test
    @DisplayName("crear con autor inexistente lanza ResourceNotFoundException")
    void crear_AutorInexistente_LanzaResourceNotFoundException() {
        // Arrange
        LibroCreateDTO dto = new LibroCreateDTO("978-012345", "Rayuela", 1L, List.of(10L, 20L));
        GeneroEntity genero = new GeneroEntity(1L, "Novela");
        when(generoDao.findEntityById(1L)).thenReturn(Optional.of(genero));
        when(autorDao.findEntitiesByIds(List.of(10L, 20L))).thenReturn(List.of(new AutorEntity(10L, "Cortázar")));

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.crear(dto));
        assertTrue(ex.getMessage().contains("20"));
        verify(libroDao, never()).save(any());
    }

    @Test
    @DisplayName("actualizar libro exitoso modifica datos y conserva entidad")
    void actualizar_DatosValidos_ActualizaCorrectamente() {
        // Arrange
        Long id = 1L;
        LibroUpdateDTO dto = new LibroUpdateDTO("978-999999", "Rayuela - Edición Crítica", 2L, List.of(10L));
        LibroEntity existing = new LibroEntity(id, "978-012345", "Rayuela", new GeneroEntity(1L, "Novela"), new HashSet<>(), true);
        GeneroEntity nuevoGenero = new GeneroEntity(2L, "Ficción");
        AutorEntity autor = new AutorEntity(10L, "Julio Cortázar");

        when(libroDao.findEntityById(id)).thenReturn(Optional.of(existing));
        when(generoDao.findEntityById(2L)).thenReturn(Optional.of(nuevoGenero));
        when(autorDao.findEntitiesByIds(List.of(10L))).thenReturn(List.of(autor));

        LibroDTO expectedDTO = new LibroDTO(id, "978-999999", "Rayuela - Edición Crítica", 2L, "Ficción",
                List.of(new AutorDTO(10L, "Julio Cortázar")), true);
        when(libroDao.save(existing)).thenReturn(expectedDTO);

        // Act
        LibroDTO result = libroService.actualizar(id, dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        assertEquals("978-999999", existing.getIsbn());
        assertEquals("Rayuela - Edición Crítica", existing.getTitulo());
        assertEquals(nuevoGenero, existing.getGenero());
        assertTrue(existing.getEstado());
        verify(libroDao).save(existing);
    }

    @Test
    @DisplayName("actualizar libro inexistente lanza ResourceNotFoundException")
    void actualizar_LibroInexistente_LanzaResourceNotFoundException() {
        // Arrange
        Long id = 99L;
        LibroUpdateDTO dto = new LibroUpdateDTO("978-999999", "Título", 1L, List.of(10L));
        when(libroDao.findEntityById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.actualizar(id, dto));
        assertTrue(ex.getMessage().contains("99"));
        verify(libroDao, never()).save(any());
    }

    @Test
    @DisplayName("inhabilitar cambia el estado a false")
    void inhabilitar_LibroExiste_CambiaEstadoAFalso() {
        // Arrange
        Long id = 1L;
        LibroEntity existing = new LibroEntity(id, "978-012345", "Rayuela", new GeneroEntity(1L, "Novela"), new HashSet<>(), true);
        when(libroDao.findEntityById(id)).thenReturn(Optional.of(existing));

        // Act
        libroService.inhabilitar(id);

        // Assert
        assertFalse(existing.getEstado());
        verify(libroDao).save(existing);
    }

    @Test
    @DisplayName("inhabilitar libro inexistente lanza ResourceNotFoundException")
    void inhabilitar_LibroInexistente_LanzaResourceNotFoundException() {
        // Arrange
        Long id = 99L;
        when(libroDao.findEntityById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.inhabilitar(id));
        assertTrue(ex.getMessage().contains("99"));
        verify(libroDao, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId cuando existe retorna LibroDTO")
    void buscarPorId_Existe_RetornaLibroDTO() {
        // Arrange
        Long id = 1L;
        LibroDTO dto = new LibroDTO(id, "978-012345", "Rayuela", 1L, "Novela", List.of(), true);
        when(libroDao.findById(id)).thenReturn(Optional.of(dto));

        // Act
        LibroDTO result = libroService.buscarPorId(id);

        // Assert
        assertEquals(dto, result);
        verify(libroDao).findById(id);
    }

    @Test
    @DisplayName("buscarPorId cuando no existe lanza ResourceNotFoundException")
    void buscarPorId_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long id = 99L;
        when(libroDao.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.buscarPorId(id));
        assertTrue(ex.getMessage().contains("99"));
        verify(libroDao).findById(id);
    }

    @Test
    @DisplayName("buscarPorTitulo retorna lista de libros del DAO")
    void buscarPorTitulo_RetornaLista() {
        List<LibroDTO> list = List.of(new LibroDTO(1L, "123", "Cien Años", 1L, "Novela", List.of(), true));
        when(libroDao.buscarPorTitulo("Cien")).thenReturn(list);

        List<LibroDTO> result = libroService.buscarPorTitulo("Cien");
        assertEquals(list, result);
        verify(libroDao).buscarPorTitulo("Cien");
    }

    @Test
    @DisplayName("buscarPorGenero retorna lista de libros del DAO")
    void buscarPorGenero_RetornaLista() {
        List<LibroDTO> list = List.of(new LibroDTO(1L, "123", "Cien Años", 1L, "Novela", List.of(), true));
        when(libroDao.buscarPorGenero("Novela")).thenReturn(list);

        List<LibroDTO> result = libroService.buscarPorGenero("Novela");
        assertEquals(list, result);
        verify(libroDao).buscarPorGenero("Novela");
    }

    @Test
    @DisplayName("buscarPorAutor retorna lista de libros del DAO")
    void buscarPorAutor_RetornaLista() {
        List<LibroDTO> list = List.of(new LibroDTO(1L, "123", "Cien Años", 1L, "Novela", List.of(), true));
        when(libroDao.buscarPorAutor("García")).thenReturn(list);

        List<LibroDTO> result = libroService.buscarPorAutor("García");
        assertEquals(list, result);
        verify(libroDao).buscarPorAutor("García");
    }

    @Test
    @DisplayName("consultarDisponibilidad con ejemplares disponibles retorna disponible=true y cantidad")
    void consultarDisponibilidad_ConEjemplares_RetornaDisponibleTrue() {
        // Arrange
        Long idLibro = 1L;
        LocalDateTime fecha = LocalDateTime.now().plusDays(2);
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(idLibro);
        libro.setEstado(true);

        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(libro));
        when(ejemplarDao.contarDisponiblesEnInstante(idLibro, fecha)).thenReturn(2);

        // Act
        DisponibilidadDTO result = libroService.consultarDisponibilidad(idLibro, fecha);

        // Assert
        assertNotNull(result);
        assertEquals(idLibro, result.getIdLibro());
        assertEquals(fecha, result.getFechaHora());
        assertTrue(result.getDisponible());
        assertEquals(2, result.getEjemplaresDisponibles());
    }

    @Test
    @DisplayName("consultarDisponibilidad sin ejemplares disponibles retorna disponible=false y 0")
    void consultarDisponibilidad_SinEjemplares_RetornaDisponibleFalse() {
        // Arrange
        Long idLibro = 1L;
        LocalDateTime fecha = LocalDateTime.now().plusDays(2);
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(idLibro);
        libro.setEstado(true);

        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(libro));
        when(ejemplarDao.contarDisponiblesEnInstante(idLibro, fecha)).thenReturn(0);

        // Act
        DisponibilidadDTO result = libroService.consultarDisponibilidad(idLibro, fecha);

        // Assert
        assertNotNull(result);
        assertFalse(result.getDisponible());
        assertEquals(0, result.getEjemplaresDisponibles());
    }

    @Test
    @DisplayName("consultarDisponibilidad de libro inhabilitado lanza ResourceNotFoundException")
    void consultarDisponibilidad_LibroInhabilitado_LanzaResourceNotFoundException() {
        // Arrange
        Long idLibro = 1L;
        LocalDateTime fecha = LocalDateTime.now().plusDays(2);
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(idLibro);
        libro.setEstado(false); // inhabilitado

        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(libro));

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.consultarDisponibilidad(idLibro, fecha));
        assertTrue(ex.getMessage().contains("1"));
        verify(ejemplarDao, never()).contarDisponiblesEnInstante(any(), any());
    }

    @Test
    @DisplayName("consultarDetalle retorna LibroDetalleDTO con total y lista de reservas")
    void consultarDetalle_LibroExiste_RetornaLibroDetalleDTO() {
        // Arrange
        Long idLibro = 1L;
        LibroDTO libroDTO = new LibroDTO(idLibro, "123", "Título", 1L, "Género", List.of(), true);
        List<ReservaDTO> reservas = List.of(
                new ReservaDTO(10L, 1L, "User 1", 101L, idLibro, "Título", LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), "ACTIVA"),
                new ReservaDTO(20L, 2L, "User 2", 102L, idLibro, "Título", LocalDateTime.now(), LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(6), "FINALIZADA")
        );

        when(libroDao.findById(idLibro)).thenReturn(Optional.of(libroDTO));
        when(reservaDao.findByLibroId(idLibro)).thenReturn(reservas);

        // Act
        LibroDetalleDTO result = libroService.consultarDetalle(idLibro);

        // Assert
        assertNotNull(result);
        assertEquals(libroDTO, result.getLibro());
        assertEquals(2, result.getTotalReservas());
        assertEquals(reservas, result.getReservas());
        verify(reservaDao).findByLibroId(idLibro);
    }

    @Test
    @DisplayName("consultarDetalle con libro inexistente lanza ResourceNotFoundException")
    void consultarDetalle_LibroNoExiste_LanzaResourceNotFoundException() {
        Long idLibro = 99L;
        when(libroDao.findById(idLibro)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> libroService.consultarDetalle(idLibro));
        assertTrue(ex.getMessage().contains("99"));
        verify(reservaDao, never()).findByLibroId(any());
    }
}
