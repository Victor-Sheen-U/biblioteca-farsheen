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
@Schema(description = "Datos públicos de un libro en el catálogo con disponibilidad actual")
public class LibroCatalogoDTO {

    @Schema(description = "Identificador único del libro", example = "1")
    private Long idLibro;

    @Schema(description = "Código ISBN", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Título del libro", example = "Clean Code: A Handbook of Agile Software Craftsmanship")
    private String titulo;

    @Schema(description = "ID del género literario", example = "1")
    private Long idGenero;

    @Schema(description = "Nombre del género literario", example = "Tecnología")
    private String nombreGenero;

    @Schema(description = "Lista de autores del libro")
    private List<AutorDTO> autores;

    @Schema(description = "Año de publicación del libro (puede ser nulo)", example = "2008")
    private Integer anioPublicacion;

    @Schema(description = "Total de ejemplares registrados para el libro", example = "3")
    private Integer totalEjemplares;

    @Schema(description = "Cantidad de ejemplares disponibles en este instante", example = "2")
    private Integer ejemplaresDisponibles;
}
