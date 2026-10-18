package com.eam.biblioteca.businessLayer.dto.genero;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un género")
public class GeneroCreateDTO {

    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(max = 80, message = "El nombre del género no puede superar 80 caracteres")
    @Schema(description = "Nombre del género literario", example = "Ciencia Ficción")
    private String nombreGenero;
}
