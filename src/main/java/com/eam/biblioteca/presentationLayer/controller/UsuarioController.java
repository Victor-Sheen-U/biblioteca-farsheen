package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioCreateDTO;
import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
import com.eam.biblioteca.businessLayer.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Endpoints de administración de usuarios (Solo ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear usuario como administrador", description = "Permite a un administrador crear usuarios con rol especificado (RFN10)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, rol inválido o duplicado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UsuarioDTO> crearComoAdmin(@Valid @RequestBody UsuarioCreateDTO request) {
        UsuarioDTO creado = usuarioService.crearComoAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Consulta un usuario por su identificador único (RFN12)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar usuario por documento", description = "Consulta un usuario por su documento de identidad (RFN12)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UsuarioDTO> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(usuarioService.buscarPorDocumento(documento));
    }

    @PatchMapping("/documento/{documento}/deshabilitar")
    @Operation(summary = "Deshabilitar usuario", description = "Inhabilita un usuario mediante su documento de identidad (RFN13)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UsuarioDTO> deshabilitarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(usuarioService.deshabilitarPorDocumento(documento));
    }
}
