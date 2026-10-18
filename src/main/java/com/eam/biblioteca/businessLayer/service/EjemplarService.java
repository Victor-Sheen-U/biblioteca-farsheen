package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarCreateDTO;
import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;

import java.util.List;

public interface EjemplarService {

    List<EjemplarDTO> crear(Long idLibro, EjemplarCreateDTO dto);

    List<EjemplarDTO> listarPorLibro(Long idLibro);
}
