package com.eam.biblioteca.businessLayer.dto.libro;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un libro")
public class LibroDTO {

    @Schema(description = "Identificador único del libro", example = "1")
    private Long idLibro;

    @Schema(description = "Código ISBN", example = "978-0-123456-47-2")
    private String isbn;

    @Schema(description = "Título del libro", example = "Cien Años de Soledad")
    private String titulo;

    @Schema(description = "ID del género", example = "1")
    private Long idGenero;

    @Schema(description = "Nombre del género", example = "Novela")
    private String nombreGenero;

    @Schema(description = "Lista de autores del libro")
    private List<AutorDTO> autores;

    @Schema(description = "Estado de habilitación del libro", example = "true")
    private Boolean estado;
}
