package com.eam.biblioteca.businessLayer.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de una reserva")
public class ReservaDTO {

    @Schema(description = "Identificador único de la reserva", example = "1")
    private Long idReserva;

    @Schema(description = "ID del usuario que realizó la reserva", example = "5")
    private Long idUsuario;

    @Schema(description = "Nombre completo del usuario", example = "Carlos Gómez")
    private String nombreUsuario;

    @Schema(description = "ID del ejemplar asignado a la reserva", example = "12")
    private Long idEjemplar;

    @Schema(description = "ID del libro reservado", example = "3")
    private Long idLibro;

    @Schema(description = "Título del libro reservado", example = "Cien Años de Soledad")
    private String tituloLibro;

    @Schema(description = "Fecha y hora en que se solicitó la reserva", example = "2026-11-10T09:30:00")
    private LocalDateTime fechaSolicitud;

    @Schema(description = "Fecha y hora de inicio de la reserva", example = "2026-11-20T10:00:00")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha y hora de entrega/fin de la reserva", example = "2026-11-25T10:00:00")
    private LocalDateTime fechaEntrega;

    @Schema(description = "Estado efectivo de la reserva (ACTIVA, FINALIZADA o CANCELADA)", example = "ACTIVA")
    private String estadoReserva;
}
