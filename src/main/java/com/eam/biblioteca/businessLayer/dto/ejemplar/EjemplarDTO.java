package com.eam.biblioteca.businessLayer.dto.ejemplar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un ejemplar")
public class EjemplarDTO {

    @Schema(description = "Identificador único del ejemplar", example = "1")
    private Long idEjemplar;

    @Schema(description = "ID del libro al que pertenece", example = "10")
    private Long idLibro;

    @Schema(description = "Estado de disponibilidad del ejemplar", example = "DISPONIBLE")
    private String estadoDisponibilidad;
}
