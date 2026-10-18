package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.persistenceLayer.mapper.UsuarioMapper;
import com.eam.biblioteca.persistenceLayer.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioDao {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public Optional<UsuarioDTO> findById(Long id) {
        return usuarioRepository.findById(id).map(usuarioMapper::toDTO);
    }

    public Optional<UsuarioDTO> findByDocumento(String documento) {
        return usuarioRepository.findByDocumentoIdentidad(documento).map(usuarioMapper::toDTO);
    }

    public Optional<UsuarioEntity> findEntityById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioEntity> findEntityByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Optional<UsuarioEntity> findEntityByDocumento(String documento) {
        return usuarioRepository.findByDocumentoIdentidad(documento);
    }

    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    public boolean existsByDocumento(String documento) {
        return usuarioRepository.existsByDocumentoIdentidad(documento);
    }

    public UsuarioDTO save(UsuarioEntity entity) {
        UsuarioEntity guardado = usuarioRepository.save(entity);
        return usuarioMapper.toDTO(guardado);
    }
}
