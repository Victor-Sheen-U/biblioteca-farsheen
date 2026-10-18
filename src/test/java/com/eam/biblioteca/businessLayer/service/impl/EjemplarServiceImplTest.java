package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarCreateDTO;
import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EjemplarServiceImplTest {

    @Mock
    private EjemplarDao ejemplarDao;

    @Mock
    private LibroDao libroDao;

    @InjectMocks
    private EjemplarServiceImpl ejemplarService;

    @Test
    @DisplayName("crear con cantidad 3 crea 3 ejemplares DISPONIBLE")
    void crear_CantidadTres_CreaTresEjemplares() {
        // Arrange
        Long idLibro = 1L;
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(idLibro);
        libro.setTitulo("Libro Test");

        EjemplarCreateDTO dto = new EjemplarCreateDTO(3);
        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(libro));

        List<EjemplarDTO> expectedList = List.of(
                new EjemplarDTO(101L, idLibro, "DISPONIBLE"),
                new EjemplarDTO(102L, idLibro, "DISPONIBLE"),
                new EjemplarDTO(103L, idLibro, "DISPONIBLE")
        );
        when(ejemplarDao.saveAll(anyList())).thenReturn(expectedList);

        // Act
        List<EjemplarDTO> result = ejemplarService.crear(idLibro, dto);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EjemplarEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(ejemplarDao).saveAll(captor.capture());
        List<EjemplarEntity> captured = captor.getValue();

        assertEquals(3, captured.size());
        for (EjemplarEntity e : captured) {
            assertEquals(libro, e.getLibro());
            assertEquals(EjemplarEntity.DISPONIBLE, e.getEstadoDisponibilidad());
        }
    }

    @Test
    @DisplayName("crear con cantidad nula o dto nulo crea 1 ejemplar")
    void crear_CantidadNula_CreaUnEjemplar() {
        // Arrange
        Long idLibro = 1L;
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(idLibro);

        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(libro));
        when(ejemplarDao.saveAll(anyList())).thenReturn(List.of(new EjemplarDTO(101L, idLibro, "DISPONIBLE")));

        // Act
        List<EjemplarDTO> result = ejemplarService.crear(idLibro, null);

        // Assert
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EjemplarEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(ejemplarDao).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    @DisplayName("crear con libro inexistente lanza ResourceNotFoundException")
    void crear_LibroInexistente_LanzaResourceNotFoundException() {
        // Arrange
        Long idLibro = 99L;
        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> ejemplarService.crear(idLibro, new EjemplarCreateDTO(1)));
        assertTrue(ex.getMessage().contains("99"));
        verify(ejemplarDao, never()).saveAll(any());
    }

    @Test
    @DisplayName("listarPorLibro cuando libro existe retorna lista de ejemplares")
    void listarPorLibro_LibroExiste_RetornaLista() {
        // Arrange
        Long idLibro = 1L;
        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.of(new LibroEntity()));
        List<EjemplarDTO> list = List.of(new EjemplarDTO(101L, idLibro, "DISPONIBLE"));
        when(ejemplarDao.findByLibroId(idLibro)).thenReturn(list);

        // Act
        List<EjemplarDTO> result = ejemplarService.listarPorLibro(idLibro);

        // Assert
        assertEquals(list, result);
        verify(ejemplarDao).findByLibroId(idLibro);
    }

    @Test
    @DisplayName("listarPorLibro cuando libro no existe lanza ResourceNotFoundException")
    void listarPorLibro_LibroNoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long idLibro = 99L;
        when(libroDao.findEntityById(idLibro)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> ejemplarService.listarPorLibro(idLibro));
        assertTrue(ex.getMessage().contains("99"));
        verify(ejemplarDao, never()).findByLibroId(any());
    }
}
