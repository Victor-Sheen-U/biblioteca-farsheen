package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile("seed")
@Order(2)
public class CatalogoSeedInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogoSeedInitializer.class);

    private final GeneroRepository generoRepository;
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;
    private final ObjectMapper objectMapper;

    public CatalogoSeedInitializer(
            GeneroRepository generoRepository,
            AutorRepository autorRepository,
            LibroRepository libroRepository,
            EjemplarRepository ejemplarRepository) {
        this.generoRepository = generoRepository;
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public void run(String... args) {
        ClassPathResource resource = new ClassPathResource("seed/catalogo.json");
        if (!resource.exists()) {
            log.warn("No se encontró seed/catalogo.json; se omite el seed de catálogo.");
            return;
        }

        List<SeedLibroItem> items;
        try (InputStream is = resource.getInputStream()) {
            items = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo seed/catalogo.json", e);
        }

        int librosCreados = 0;
        int ejemplaresCreados = 0;

        for (SeedLibroItem item : items) {
            if (libroRepository.findByIsbn(item.isbn()).isPresent()) {
                continue;
            }

            GeneroEntity genero = generoRepository.findByNombreGeneroIgnoreCase(item.genero())
                    .orElseGet(() -> generoRepository.save(new GeneroEntity(null, item.genero())));

            Set<AutorEntity> autores = new LinkedHashSet<>();
            for (String nombreAutor : item.autores()) {
                AutorEntity autor = autorRepository.findByNombreAutorIgnoreCase(nombreAutor)
                        .orElseGet(() -> autorRepository.save(new AutorEntity(null, nombreAutor)));
                autores.add(autor);
            }

            LibroEntity libro = new LibroEntity();
            libro.setIsbn(item.isbn());
            libro.setTitulo(item.titulo());
            libro.setGenero(genero);
            libro.setAutores(autores);
            libro.setAnioPublicacion(item.anioPublicacion());
            libro.setEstado(true);
            LibroEntity guardado = libroRepository.save(libro);
            librosCreados++;

            int cantidad = item.ejemplares() != null ? item.ejemplares() : 0;
            if (cantidad > 0) {
                List<EjemplarEntity> nuevos = new ArrayList<>(cantidad);
                for (int i = 0; i < cantidad; i++) {
                    nuevos.add(new EjemplarEntity(null, guardado, EjemplarEntity.DISPONIBLE));
                }
                ejemplarRepository.saveAll(nuevos);
                ejemplaresCreados += cantidad;
            }
        }

        log.info("Seed de catálogo finalizado: {} libros nuevos y {} ejemplares creados.", librosCreados, ejemplaresCreados);
    }

    private record SeedLibroItem(
            String isbn,
            String titulo,
            String genero,
            List<String> autores,
            Integer anioPublicacion,
            Integer ejemplares) {
    }
}
