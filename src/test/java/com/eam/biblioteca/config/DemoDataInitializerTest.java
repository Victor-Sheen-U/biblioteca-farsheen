package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import com.eam.biblioteca.persistenceLayer.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DemoDataInitializerTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Con seedDemoData=false por defecto en test no se siembran libros demo")
    void run_ConSeedDesactivado_NoSiembraLibros() {
        DemoDataInitializer initializer = new DemoDataInitializer(
                false, generoRepository, autorRepository, libroRepository, ejemplarRepository, usuarioDao, passwordEncoder
        );

        long librosAntes = libroRepository.count();
        initializer.run();

        assertEquals(librosAntes, libroRepository.count());
    }

    @Test
    @DisplayName("Con seedDemoData=true siembra >=5 géneros, >=12 autores, >=16 libros con ejemplares y es idempotente")
    void run_ConSeedActivado_SiembraDatosYEsIdempotente() {
        DemoDataInitializer initializer = new DemoDataInitializer(
                true, generoRepository, autorRepository, libroRepository, ejemplarRepository, usuarioDao, passwordEncoder
        );

        initializer.run();

        long totalGeneros = generoRepository.count();
        long totalAutores = autorRepository.count();
        long totalLibros = libroRepository.count();
        long totalEjemplares = ejemplarRepository.count();

        assertTrue(totalGeneros >= 5, "Debe sembrar al menos 5 géneros");
        assertTrue(totalAutores >= 12, "Debe sembrar al menos 12 autores");
        assertTrue(totalLibros >= 16, "Debe sembrar al menos 16 libros");
        assertTrue(totalEjemplares >= 16, "Cada libro debe tener al menos un ejemplar");
        assertTrue(usuarioRepository.existsByCorreo("usuario@biblioteca.com"), "Debe crear el usuario lector demo");

        long librosSinAnio = libroRepository.findAll().stream()
                .filter(l -> l.getAnioPublicacion() == null)
                .count();
        assertTrue(librosSinAnio >= 2, "Al menos 2 libros deben tener anioPublicacion null");

        // Segunda ejecución: verificar idempotencia
        initializer.run();

        assertEquals(totalGeneros, generoRepository.count());
        assertEquals(totalAutores, autorRepository.count());
        assertEquals(totalLibros, libroRepository.count());
        assertEquals(totalEjemplares, ejemplarRepository.count());
    }
}
