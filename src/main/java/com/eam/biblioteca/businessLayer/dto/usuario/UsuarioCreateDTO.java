package com.eam.biblioteca.businessLayer.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para la creación de un usuario")
public class UsuarioCreateDTO {

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Size(min = 3, max = 20, message = "El documento de identidad debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "El documento de identidad solo puede contener letras, números, puntos, guiones o guión bajo")
    @Schema(description = "Documento de identidad único del usuario", example = "1094900123")
    private String documentoIdentidad;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre completo no puede superar 150 caracteres")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez Gómez")
    private String nombreCompleto;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Size(max = 120, message = "El correo electrónico no puede superar 120 caracteres")
    @Schema(description = "Correo electrónico único", example = "juan.perez@biblioteca.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
    @Schema(description = "Contraseña en texto plano para registro", example = "ClaveSegura123*")
    private String password;

    @Size(max = 10, message = "El rol no puede superar 10 caracteres")
    @Schema(description = "Rol del usuario (ADMIN o USUARIO), opcional", example = "USUARIO")
    private String rol;
}
