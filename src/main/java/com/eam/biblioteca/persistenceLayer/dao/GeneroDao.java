package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.mapper.GeneroMapper;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GeneroDao {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    public List<GeneroDTO> findAll() {
        return generoMapper.toDTOList(generoRepository.findAll());
    }

    public Optional<GeneroEntity> findEntityById(Long id) {
        return generoRepository.findById(id);
    }

    public boolean existsByNombre(String nombre) {
        return generoRepository.existsByNombreGeneroIgnoreCase(nombre);
    }

    public GeneroDTO save(GeneroEntity entity) {
        GeneroEntity guardado = generoRepository.save(entity);
        return generoMapper.toDTO(guardado);
    }
}
