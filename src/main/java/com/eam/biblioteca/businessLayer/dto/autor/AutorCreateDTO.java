package com.eam.biblioteca.businessLayer.dto.autor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un nuevo autor")
public class AutorCreateDTO {

    @NotBlank(message = "El nombre del autor es obligatorio")
    @Size(max = 150, message = "El nombre del autor no puede superar 150 caracteres")
    @Schema(description = "Nombre completo del autor", example = "Gabriel García Márquez")
    private String nombreAutor;
}
