package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.libro.LibroCatalogoDTO;
import com.eam.biblioteca.businessLayer.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo Público", description = "Endpoints públicos de solo lectura para consultar el catálogo de libros habilitados")
public class CatalogoController {

    private final LibroService libroService;

    @GetMapping
    @Operation(summary = "Listar catálogo público", description = "Retorna todos los libros habilitados ordenados por título con su disponibilidad actual (Público)")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido exitosamente")
    public ResponseEntity<List<LibroCatalogoDTO>> listarCatalogo() {
        return ResponseEntity.ok(libroService.listarCatalogoPublico());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar libro del catálogo por ID", description = "Retorna los datos públicos y disponibilidad actual de un libro habilitado (Público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado o inhabilitado")
    })
    public ResponseEntity<LibroCatalogoDTO> obtenerLibroCatalogo(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerLibroCatalogoPublico(id));
    }
}
