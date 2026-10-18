package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.businessLayer.service.GeneroService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/generos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Géneros", description = "Endpoints de gestión de géneros literarios")
public class GeneroController {

    private final GeneroService generoService;

    @PostMapping
    @Operation(summary = "Crear nuevo género", description = "Permite registrar un nuevo género literario (Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Género creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nombre de género duplicado o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<GeneroDTO> crear(@Valid @RequestBody GeneroCreateDTO request) {
        GeneroDTO creado = generoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar géneros", description = "Retorna todos los géneros literarios registrados (Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de géneros obtenida exitosamente")
    public ResponseEntity<List<GeneroDTO>> listar() {
        return ResponseEntity.ok(generoService.listar());
    }
}
