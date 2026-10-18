package com.eam.biblioteca.businessLayer.dto.libro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un libro existente")
public class LibroUpdateDTO {

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(max = 20, message = "El ISBN no puede superar 20 caracteres")
    @Schema(description = "Código ISBN del libro", example = "978-0-123456-47-2")
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    @Schema(description = "Título del libro", example = "Cien Años de Soledad - Edición Ilustrada")
    private String titulo;

    @NotNull(message = "El ID del género es obligatorio")
    @Schema(description = "Identificador del género literario", example = "1")
    private Long idGenero;

    @NotEmpty(message = "Debe indicar al menos un autor")
    @Schema(description = "Lista de identificadores de autores", example = "[1]")
    private List<Long> idsAutores;
}
