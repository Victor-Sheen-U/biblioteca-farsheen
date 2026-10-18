package com.eam.biblioteca.businessLayer.dto.autor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un autor")
public class AutorDTO {

    @Schema(description = "Identificador único del autor", example = "1")
    private Long idAutor;

    @Schema(description = "Nombre completo del autor", example = "Gabriel García Márquez")
    private String nombreAutor;
}
