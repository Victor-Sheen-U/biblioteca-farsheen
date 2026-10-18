package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
import com.eam.biblioteca.businessLayer.service.UsuarioService;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDTO registrar(UsuarioCreateDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();
        validarUnicidad(dto.getDocumentoIdentidad(), correo);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(UsuarioEntity.ROL_USUARIO);
        usuario.setEstado(true);

        return usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO crearComoAdmin(UsuarioCreateDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();
        validarUnicidad(dto.getDocumentoIdentidad(), correo);

        String rol = UsuarioEntity.ROL_USUARIO;
        if (dto.getRol() != null && !dto.getRol().isBlank()) {
            String rolTrim = dto.getRol().trim();
            if (rolTrim.equalsIgnoreCase(UsuarioEntity.ROL_ADMIN)) {
                rol = UsuarioEntity.ROL_ADMIN;
            } else if (rolTrim.equalsIgnoreCase(UsuarioEntity.ROL_USUARIO)) {
                rol = UsuarioEntity.ROL_USUARIO;
            } else {
                throw new BadRequestException("Rol inválido: " + dto.getRol());
            }
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado(true);

        return usuarioDao.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorDocumento(String documento) {
        return usuarioDao.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con documento: " + documento));
    }

    @Override
    @Transactional
    public UsuarioDTO deshabilitarPorDocumento(String documento) {
        UsuarioEntity usuario = usuarioDao.findEntityByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con documento: " + documento));

        usuario.setEstado(false);
        return usuarioDao.save(usuario);
    }

    private void validarUnicidad(String documento, String correo) {
        if (usuarioDao.existsByDocumento(documento)) {
            throw new BadRequestException("Ya existe un usuario registrado con el documento: " + documento);
        }
        if (usuarioDao.existsByCorreo(correo)) {
            throw new BadRequestException("Ya existe un usuario registrado con el correo: " + correo);
        }
    }
}
