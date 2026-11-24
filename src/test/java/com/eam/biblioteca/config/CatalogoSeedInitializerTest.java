package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import com.eam.biblioteca.persistenceLayer.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogoSeedInitializerTest {

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("CatalogoSeedInitializer carga >=40 libros reales, >=10 géneros, no crea usuarios y es idempotente")
    void run_CargaCatalogoRealYEsIdempotente() {
        long usuariosAntes = usuarioRepository.count();

        CatalogoSeedInitializer initializer = new CatalogoSeedInitializer(
                generoRepository, autorRepository, libroRepository, ejemplarRepository
        );

        initializer.run();

        long totalGeneros = generoRepository.count();
        long totalLibros = libroRepository.count();
        long totalEjemplares = ejemplarRepository.count();

        assertTrue(totalGeneros >= 10, "Debe cargar al menos 10 géneros");
        assertTrue(totalLibros >= 40, "Debe cargar al menos 40 libros");
        assertTrue(totalEjemplares >= 40, "Debe cargar ejemplares");
        assertEquals(usuariosAntes, usuarioRepository.count(), "El seed de catálogo no debe crear usuarios");

        // Re-ejecución idempotente
        initializer.run();

        assertEquals(totalGeneros, generoRepository.count());
        assertEquals(totalLibros, libroRepository.count());
        assertEquals(totalEjemplares, ejemplarRepository.count());
    }
}
