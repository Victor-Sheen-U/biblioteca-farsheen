package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarCreateDTO;
import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.businessLayer.service.EjemplarService;
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
@RequestMapping("/api/v1/libros/{idLibro}/ejemplares")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ejemplares", description = "Endpoints de soporte para gestión de ejemplares físicos (Solo ADMIN)")
public class EjemplarController {

    private final EjemplarService ejemplarService;

    @PostMapping
    @Operation(summary = "Crear ejemplares para un libro", description = "Registra una o más copias físicas para un libro existente (Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ejemplares creados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<EjemplarDTO>> crear(
            @PathVariable Long idLibro,
            @Valid @RequestBody(required = false) EjemplarCreateDTO request) {
        List<EjemplarDTO> creados = ejemplarService.crear(idLibro, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creados);
    }

    @GetMapping
    @Operation(summary = "Listar ejemplares de un libro", description = "Retorna todas las copias físicas de un libro específico (Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ejemplares obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<EjemplarDTO>> listarPorLibro(@PathVariable Long idLibro) {
        return ResponseEntity.ok(ejemplarService.listarPorLibro(idLibro));
    }
}
