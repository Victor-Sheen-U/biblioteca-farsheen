package com.eam.biblioteca.businessLayer.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una reserva de libro")
public class ReservaCreateDTO {

    @NotNull(message = "El ID del libro es obligatorio")
    @Schema(description = "Identificador del libro a reservar", example = "1")
    private Long idLibro;

    @NotNull(message = "La fecha de inicio de la reserva es obligatoria")
    @Schema(description = "Fecha y hora de inicio de la reserva (formato ISO)", example = "2026-11-20T10:00:00")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de entrega de la reserva es obligatoria")
    @Schema(description = "Fecha y hora de entrega/fin de la reserva (formato ISO)", example = "2026-11-25T10:00:00")
    private LocalDateTime fechaEntrega;
}
