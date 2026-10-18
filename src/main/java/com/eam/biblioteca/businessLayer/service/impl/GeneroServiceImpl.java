package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.businessLayer.service.GeneroService;
import com.eam.biblioteca.persistenceLayer.dao.GeneroDao;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneroServiceImpl implements GeneroService {

    private final GeneroDao generoDao;

    @Override
    @Transactional
    public GeneroDTO crear(GeneroCreateDTO dto) {
        String nombre = dto.getNombreGenero().trim();
        if (generoDao.existsByNombre(nombre)) {
            throw new BadRequestException("Ya existe un género con el nombre: " + dto.getNombreGenero());
        }

        GeneroEntity genero = new GeneroEntity();
        genero.setNombreGenero(nombre);
        return generoDao.save(genero);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneroDTO> listar() {
        return generoDao.findAll();
    }
}
