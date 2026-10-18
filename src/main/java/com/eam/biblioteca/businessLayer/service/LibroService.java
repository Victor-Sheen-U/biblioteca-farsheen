package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.libro.DisponibilidadDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroDetalleDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface LibroService {

    LibroDTO crear(LibroCreateDTO dto);

    LibroDTO actualizar(Long id, LibroUpdateDTO dto);

    void inhabilitar(Long id);

    LibroDTO buscarPorId(Long id);

    List<LibroDTO> buscarPorTitulo(String titulo);

    List<LibroDTO> buscarPorGenero(String genero);

    List<LibroDTO> buscarPorAutor(String autor);

    DisponibilidadDTO consultarDisponibilidad(Long idLibro, LocalDateTime fechaHora);

    LibroDetalleDTO consultarDetalle(Long idLibro);
}
