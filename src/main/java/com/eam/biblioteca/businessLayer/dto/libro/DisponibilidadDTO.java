package com.eam.biblioteca.businessLayer.dto.libro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de disponibilidad de un libro en una fecha y hora")
public class DisponibilidadDTO {

    @Schema(description = "Identificador del libro", example = "1")
    private Long idLibro;

    @Schema(description = "Fecha y hora consultada", example = "2026-11-20T10:00:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Indica si hay disponibilidad", example = "true")
    private Boolean disponible;

    @Schema(description = "Cantidad de ejemplares disponibles en el instante consultado", example = "2")
    private Integer ejemplaresDisponibles;
}
