package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.mapper.AutorMapper;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AutorDao {

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

    public List<AutorDTO> findAll() {
        return autorMapper.toDTOList(autorRepository.findAll());
    }

    public List<AutorEntity> findEntitiesByIds(Collection<Long> ids) {
        return autorRepository.findAllById(ids);
    }

    public AutorDTO save(AutorEntity entity) {
        AutorEntity guardado = autorRepository.save(entity);
        return autorMapper.toDTO(guardado);
    }
}
