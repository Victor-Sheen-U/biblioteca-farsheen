package com.eam.biblioteca.businessLayer.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponseDTO {

    @Schema(description = "Token JWT generado", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipoToken;

    @Schema(description = "Tiempo de expiración en milisegundos", example = "7200000")
    private Long expiraEnMs;

    @Schema(description = "Correo del usuario autenticado", example = "admin@biblioteca.com")
    private String correo;

    @Schema(description = "Rol del usuario autenticado", example = "ADMIN")
    private String rol;

    @Schema(description = "Nombre completo del usuario autenticado", example = "Administrador")
    private String nombreCompleto;

    public AuthResponseDTO(String token, String tipoToken, Long expiraEnMs, String correo, String rol) {
        this(token, tipoToken, expiraEnMs, correo, rol, null);
    }
}
