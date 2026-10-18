package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;

import java.util.List;

public interface GeneroService {

    GeneroDTO crear(GeneroCreateDTO dto);

    List<GeneroDTO> listar();
}
