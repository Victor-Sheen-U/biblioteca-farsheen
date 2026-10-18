package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.autor.AutorCreateDTO;
import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.businessLayer.service.AutorService;
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
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Autores", description = "Endpoints de gestión de autores")
public class AutorController {

    private final AutorService autorService;

    @PostMapping
    @Operation(summary = "Crear nuevo autor", description = "Permite registrar un nuevo autor en el sistema (Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Autor registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del autor inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<AutorDTO> crear(@Valid @RequestBody AutorCreateDTO request) {
        AutorDTO creado = autorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar autores", description = "Retorna todos los autores registrados (Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de autores obtenida exitosamente")
    public ResponseEntity<List<AutorDTO>> listar() {
        return ResponseEntity.ok(autorService.listar());
    }
}
