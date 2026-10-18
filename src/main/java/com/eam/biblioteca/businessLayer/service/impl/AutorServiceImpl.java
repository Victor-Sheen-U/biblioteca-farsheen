package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.autor.AutorCreateDTO;
import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.businessLayer.service.AutorService;
import com.eam.biblioteca.persistenceLayer.dao.AutorDao;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorServiceImpl implements AutorService {

    private final AutorDao autorDao;

    @Override
    @Transactional
    public AutorDTO crear(AutorCreateDTO dto) {
        AutorEntity autor = new AutorEntity();
        autor.setNombreAutor(dto.getNombreAutor().trim());
        return autorDao.save(autor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorDTO> listar() {
        return autorDao.findAll();
    }
}
