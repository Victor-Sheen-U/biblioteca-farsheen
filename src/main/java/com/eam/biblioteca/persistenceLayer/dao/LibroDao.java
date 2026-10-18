package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.libro.LibroDTO;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.mapper.LibroMapper;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LibroDao {

    private final LibroRepository libroRepository;
    private final LibroMapper libroMapper;

    public Optional<LibroDTO> findById(Long id) {
        return libroRepository.findById(id).map(libroMapper::toDTO);
    }

    public Optional<LibroEntity> findEntityById(Long id) {
        return libroRepository.findById(id);
    }

    public List<LibroDTO> buscarPorTitulo(String titulo) {
        return libroMapper.toDTOList(libroRepository.buscarPorTitulo(titulo));
    }

    public List<LibroDTO> buscarPorGenero(String genero) {
        return libroMapper.toDTOList(libroRepository.buscarPorGenero(genero));
    }

    public List<LibroDTO> buscarPorAutor(String autor) {
        return libroMapper.toDTOList(libroRepository.buscarPorAutor(autor));
    }

    public LibroDTO save(LibroEntity entity) {
        LibroEntity guardado = libroRepository.save(entity);
        return libroMapper.toDTO(guardado);
    }
}
