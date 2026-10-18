package com.eam.biblioteca.businessLayer.dto.ejemplar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear ejemplares de un libro")
public class EjemplarCreateDTO {

    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    @Schema(description = "Cantidad de ejemplares a crear (si es nulo se crea 1)", example = "2")
    private Integer cantidad;
}
