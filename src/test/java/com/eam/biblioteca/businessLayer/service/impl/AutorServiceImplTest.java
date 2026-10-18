package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.autor.AutorCreateDTO;
import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.persistenceLayer.dao.AutorDao;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorServiceImplTest {

    @Mock
    private AutorDao autorDao;

    @InjectMocks
    private AutorServiceImpl autorService;

    @Test
    @DisplayName("crear autor guarda y retorna AutorDTO")
    void crear_DatosValidos_GuardaAutorCorrectamente() {
        // Arrange
        AutorCreateDTO dto = new AutorCreateDTO("  Mario Vargas Llosa  ");
        AutorDTO expectedDTO = new AutorDTO(1L, "Mario Vargas Llosa");
        when(autorDao.save(any(AutorEntity.class))).thenReturn(expectedDTO);

        // Act
        AutorDTO result = autorService.crear(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<AutorEntity> captor = ArgumentCaptor.forClass(AutorEntity.class);
        verify(autorDao).save(captor.capture());
        assertEquals("Mario Vargas Llosa", captor.getValue().getNombreAutor());
    }

    @Test
    @DisplayName("listar retorna la lista de autores dada por el DAO")
    void listar_RetornaListaDeAutores() {
        // Arrange
        List<AutorDTO> expectedList = List.of(
                new AutorDTO(1L, "Gabriel García Márquez"),
                new AutorDTO(2L, "Jorge Luis Borges")
        );
        when(autorDao.findAll()).thenReturn(expectedList);

        // Act
        List<AutorDTO> result = autorService.listar();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(autorDao).findAll();
    }
}
