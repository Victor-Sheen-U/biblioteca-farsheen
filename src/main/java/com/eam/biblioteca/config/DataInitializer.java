package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;
    private final String adminDocumento;
    private final String adminNombre;
    private final String adminCorreo;
    private final String adminPassword;

    public DataInitializer(UsuarioDao usuarioDao,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.documento}") String adminDocumento,
                           @Value("${app.admin.nombre}") String adminNombre,
                           @Value("${app.admin.correo}") String adminCorreo,
                           @Value("${app.admin.password}") String adminPassword) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
        this.adminDocumento = adminDocumento;
        this.adminNombre = adminNombre;
        this.adminCorreo = adminCorreo;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        String correoNormalizado = adminCorreo.trim().toLowerCase();
        if (!usuarioDao.existsByCorreo(correoNormalizado)) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setDocumentoIdentidad(adminDocumento);
            admin.setNombreCompleto(adminNombre);
            admin.setCorreo(correoNormalizado);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRol(UsuarioEntity.ROL_ADMIN);
            admin.setEstado(true);

            usuarioDao.save(admin);
        }
    }
}
