package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.security.JwtAuthenticationFilter;
import com.eam.biblioteca.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UsuarioDao usuarioDao;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, usuarioDao);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Rutas exclusivas ADMIN
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/generos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/autores").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/libros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/libros/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/libros/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/libros/*/detalle").hasRole("ADMIN")
                        .requestMatchers("/api/v1/libros/*/ejemplares/**", "/api/v1/libros/*/ejemplares").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/libros/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reservas/*/cancelar-admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/usuario/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas").hasRole("ADMIN")

                        // 3. Rutas para USUARIO o ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/v1/generos").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/autores").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/libros/buscar/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/libros/*/disponibilidad").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservas").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/mis-reservas").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/*").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reservas/*/cancelar").hasAnyRole("USUARIO", "ADMIN")

                        // 4. Cualquier otra petición
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                escribirError(response, HttpServletResponse.SC_UNAUTHORIZED, "No autenticado", "Token ausente, inválido o expirado");
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                escribirError(response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado", "No tiene permisos para acceder a este recurso");
    }

    private void escribirError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String timestamp = LocalDateTime.now().toString();
        String json = "{\"timestamp\":\"" + timestamp + "\",\"status\":" + status + ",\"error\":\"" + error + "\",\"message\":\"" + message + "\"}";
        response.getWriter().write(json);
    }
}
