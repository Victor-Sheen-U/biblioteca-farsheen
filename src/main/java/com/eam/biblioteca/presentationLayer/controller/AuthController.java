package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.auth.AuthResponseDTO;
import com.eam.biblioteca.businessLayer.dto.auth.LoginRequestDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
import com.eam.biblioteca.businessLayer.service.AuthService;
import com.eam.biblioteca.businessLayer.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro e inicio de sesión")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Permite registrar un usuario con rol USUARIO por defecto (RFN10)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o correo/documento duplicado")
    })
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody UsuarioCreateDTO request) {
        UsuarioDTO creado = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario mediante correo y contraseña devolviendo un token JWT (RFN11)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o cuenta deshabilitada")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO respuesta = authService.login(request);
        return ResponseEntity.ok(respuesta);
    }
}
