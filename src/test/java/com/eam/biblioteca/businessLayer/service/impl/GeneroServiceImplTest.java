package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.persistenceLayer.dao.GeneroDao;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
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
class GeneroServiceImplTest {

    @Mock
    private GeneroDao generoDao;

    @InjectMocks
    private GeneroServiceImpl generoService;

    @Test
    @DisplayName("crear con nombre nuevo guarda y retorna GeneroDTO")
    void crear_NombreNuevo_GuardaGeneroCorrectamente() {
        // Arrange
        GeneroCreateDTO dto = new GeneroCreateDTO("  Fantasía Épica  ");
        when(generoDao.existsByNombre("Fantasía Épica")).thenReturn(false);

        GeneroDTO expectedDTO = new GeneroDTO(1L, "Fantasía Épica");
        when(generoDao.save(any(GeneroEntity.class))).thenReturn(expectedDTO);

        // Act
        GeneroDTO result = generoService.crear(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<GeneroEntity> captor = ArgumentCaptor.forClass(GeneroEntity.class);
        verify(generoDao).save(captor.capture());
        assertEquals("Fantasía Épica", captor.getValue().getNombreGenero());
    }

    @Test
    @DisplayName("crear con nombre existente lanza BadRequestException y nunca llama a save")
    void crear_NombreExistente_LanzaBadRequestException() {
        // Arrange
        GeneroCreateDTO dto = new GeneroCreateDTO("Terror");
        when(generoDao.existsByNombre("Terror")).thenReturn(true);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> generoService.crear(dto));
        assertTrue(ex.getMessage().contains("Terror"));
        verify(generoDao, never()).save(any());
    }

    @Test
    @DisplayName("listar retorna la lista de géneros proporcionada por el DAO")
    void listar_RetornaListaDeGeneros() {
        // Arrange
        List<GeneroDTO> expectedList = List.of(
                new GeneroDTO(1L, "Ciencia Ficción"),
                new GeneroDTO(2L, "Misterio")
        );
        when(generoDao.findAll()).thenReturn(expectedList);

        // Act
        List<GeneroDTO> result = generoService.listar();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(generoDao).findAll();
    }
}
