package com.eam.biblioteca.businessLayer.dto.genero;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un género")
public class GeneroDTO {

    @Schema(description = "Identificador único del género", example = "1")
    private Long idGenero;

    @Schema(description = "Nombre del género", example = "Ciencia Ficción")
    private String nombreGenero;
}
