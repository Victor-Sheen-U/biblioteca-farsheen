package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.auth.AuthResponseDTO;
import com.eam.biblioteca.businessLayer.dto.auth.LoginRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO dto);
}
