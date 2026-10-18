package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.auth.AuthResponseDTO;
import com.eam.biblioteca.businessLayer.dto.auth.LoginRequestDTO;
import com.eam.biblioteca.businessLayer.service.AuthService;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.UnauthorizedException;
import com.eam.biblioteca.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();

        UsuarioEntity usuario = usuarioDao.findEntityByCorreo(correo)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (usuario.getEstado() == null || !usuario.getEstado()) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
        return new AuthResponseDTO(token, "Bearer", jwtService.getExpirationMs(), usuario.getCorreo(), usuario.getRol());
    }
}
