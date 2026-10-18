package com.eam.biblioteca.presentationLayer.controller;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaCreateDTO;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.businessLayer.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reservas", description = "Endpoints de control y gestión de reservas (RFN14–RFN20)")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Registrar reserva", description = "Registra una reserva para un libro en un rango de fechas (RFN14 - Autenticado)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas o sin ejemplares disponibles"),
            @ApiResponse(responseCode = "404", description = "Libro o usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ReservaDTO> crear(
            Authentication authentication,
            @Valid @RequestBody ReservaCreateDTO request) {
        String correo = authentication.getName();
        ReservaDTO creada = reservaService.crear(correo, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Listar mis reservas", description = "Consulta todas las reservas realizadas por el usuario autenticado (RFN18 - Autenticado)")
    @ApiResponse(responseCode = "200", description = "Lista de reservas propias obtenida exitosamente")
    public ResponseEntity<List<ReservaDTO>> listarMisReservas(Authentication authentication) {
        String correo = authentication.getName();
        return ResponseEntity.ok(reservaService.listarMisReservas(correo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID", description = "Consulta el estado de una reserva (RFN15 - Propietario o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado a reserva ajena")
    })
    public ResponseEntity<ReservaDTO> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        String correo = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(reservaService.buscarPorId(id, correo, esAdmin));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Muestra todas las reservas registradas en el sistema (RFN16 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista global de reservas obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservaDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar reservas de un usuario", description = "Filtra las reservas de un usuario específico para auditoría (RFN17 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservaDTO>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(idUsuario));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva (Usuario)", description = "Cancela una reserva propia mientras no esté finalizada y hasta 24h antes del inicio (RFN19 - Autenticado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cancelación fuera de plazo o ya finalizada/cancelada"),
            @ApiResponse(responseCode = "403", description = "Reserva pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaDTO> cancelarComoUsuario(
            @PathVariable Long id,
            Authentication authentication) {
        String correo = authentication.getName();
        return ResponseEntity.ok(reservaService.cancelarComoUsuario(id, correo));
    }

    @PatchMapping("/{id}/cancelar-admin")
    @Operation(summary = "Cancelar una reserva (Admin)", description = "Cancela una reserva sin restricciones temporales (RFN20 - Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente por administrador"),
            @ApiResponse(responseCode = "400", description = "La reserva ya estaba cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ReservaDTO> cancelarComoAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarComoAdmin(id));
    }
}
