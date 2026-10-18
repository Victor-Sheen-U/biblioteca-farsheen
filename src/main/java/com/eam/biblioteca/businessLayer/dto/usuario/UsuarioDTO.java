package com.eam.biblioteca.businessLayer.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un usuario")
public class UsuarioDTO {

    @Schema(description = "Identificador único del usuario", example = "1")
    private Long idUsuario;

    @Schema(description = "Documento de identidad", example = "1094900123")
    private String documentoIdentidad;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez Gómez")
    private String nombreCompleto;

    @Schema(description = "Correo electrónico", example = "juan.perez@biblioteca.com")
    private String correo;

    @Schema(description = "Rol asignado al usuario", example = "USUARIO")
    private String rol;

    @Schema(description = "Estado de habilitación de la cuenta", example = "true")
    private Boolean estado;
}
