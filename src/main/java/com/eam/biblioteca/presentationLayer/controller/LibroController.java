package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.libro.*;
import com.eam.biblioteca.businessLayer.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Libros", description = "Endpoints de gestión y consulta de libros (RFN1–RFN9)")
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    @Operation(summary = "Agregar libro", description = "Registra un nuevo libro en la biblioteca (RFN1 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Libro registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Género o autor no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<LibroDTO> crear(@Valid @RequestBody LibroCreateDTO request) {
        LibroDTO creado = libroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro (inhabilitar)", description = "Inhabilita un libro para que no sea visible para usuarios (RFN2 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Libro inhabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Void> inhabilitar(@PathVariable Long id) {
        libroService.inhabilitar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar libro por ID", description = "Consulta la información de un libro por su identificador único (RFN3 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<LibroDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.buscarPorId(id));
    }

    @GetMapping("/buscar/titulo")
    @Operation(summary = "Buscar libro por nombre/título", description = "Busca libros habilitados que coincidan con el título (RFN4 - Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    public ResponseEntity<List<LibroDTO>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(libroService.buscarPorTitulo(titulo));
    }

    @GetMapping("/buscar/genero")
    @Operation(summary = "Buscar libro por género", description = "Busca libros habilitados por su género literario (RFN5 - Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    public ResponseEntity<List<LibroDTO>> buscarPorGenero(@RequestParam String genero) {
        return ResponseEntity.ok(libroService.buscarPorGenero(genero));
    }

    @GetMapping("/buscar/autor")
    @Operation(summary = "Buscar libro por autor", description = "Busca libros habilitados que tengan como autor al consultado (RFN6 - Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    public ResponseEntity<List<LibroDTO>> buscarPorAutor(@RequestParam String autor) {
        return ResponseEntity.ok(libroService.buscarPorAutor(autor));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar libro", description = "Actualiza los datos de un libro registrado (RFN7 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Libro, género o autor no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<LibroDTO> actualizar(@PathVariable Long id, @Valid @RequestBody LibroUpdateDTO request) {
        return ResponseEntity.ok(libroService.actualizar(id, request));
    }

    @GetMapping("/{id}/disponibilidad")
    @Operation(summary = "Consultar disponibilidad de un libro", description = "Determina si el libro tiene ejemplares disponibles en una fecha y hora (RFN8 - Autenticado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad consultada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado o inhabilitado")
    })
    public ResponseEntity<DisponibilidadDTO> consultarDisponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return ResponseEntity.ok(libroService.consultarDisponibilidad(id, fechaHora));
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Consultar información detallada del libro", description = "Muestra metadatos y total de reservas históricas (RFN9 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<LibroDetalleDTO> consultarDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.consultarDetalle(id));
    }
}
