package com.eam.biblioteca.businessLayer.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para iniciar sesión")
public class LoginRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo es inválido")
    @Size(max = 120, message = "El correo no puede superar 120 caracteres")
    @Schema(description = "Correo electrónico registrado", example = "admin@biblioteca.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 60, message = "La contraseña no puede superar 60 caracteres")
    @Schema(description = "Contraseña del usuario", example = "Admin12345*")
    private String password;
}
