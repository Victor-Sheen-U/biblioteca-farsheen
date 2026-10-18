package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;

public interface UsuarioService {

    UsuarioDTO registrar(UsuarioCreateDTO dto);

    UsuarioDTO crearComoAdmin(UsuarioCreateDTO dto);

    UsuarioDTO buscarPorId(Long id);

    UsuarioDTO buscarPorDocumento(String documento);

    UsuarioDTO deshabilitarPorDocumento(String documento);
}
