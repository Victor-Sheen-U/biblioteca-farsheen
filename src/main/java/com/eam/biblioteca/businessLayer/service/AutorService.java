package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.autor.AutorCreateDTO;
import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;

import java.util.List;

public interface AutorService {

    AutorDTO crear(AutorCreateDTO dto);

    List<AutorDTO> listar();
}
