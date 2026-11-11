package com.eam.biblioteca.config;

import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.persistenceLayer.repository.AutorRepository;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import com.eam.biblioteca.persistenceLayer.repository.GeneroRepository;
import com.eam.biblioteca.persistenceLayer.repository.LibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(2)
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final boolean seedDemoData;
    private final GeneroRepository generoRepository;
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(
            @Value("${app.seed.demo-data:false}") boolean seedDemoData,
            GeneroRepository generoRepository,
            AutorRepository autorRepository,
            LibroRepository libroRepository,
            EjemplarRepository ejemplarRepository,
            UsuarioDao usuarioDao,
            PasswordEncoder passwordEncoder) {
        this.seedDemoData = seedDemoData;
        this.generoRepository = generoRepository;
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDemoData) {
            return;
        }
        if (libroRepository.count() > 0) {
            log.info("DemoDataInitializer omitido: ya existen libros en la base de datos.");
            return;
        }

        log.info("Sembrando datos de demostración (SEED_DEMO_DATA=true)...");

        // 1. Usuario lector demo
        String correoDemo = "usuario@biblioteca.com";
        if (!usuarioDao.existsByCorreo(correoDemo) && !usuarioDao.existsByDocumento("1094000111")) {
            UsuarioEntity lector = new UsuarioEntity();
            lector.setDocumentoIdentidad("1094000111");
            lector.setNombreCompleto("Lector Demo Biblioteca");
            lector.setCorreo(correoDemo);
            lector.setPassword(passwordEncoder.encode("Usuario123*"));
            lector.setRol(UsuarioEntity.ROL_USUARIO);
            lector.setEstado(true);
            usuarioDao.save(lector);
        }

        // 2. Géneros (6 géneros >= 5)
        GeneroEntity gTecnologia = crearGenero("Tecnología");
        GeneroEntity gHistoria = crearGenero("Historia");
        GeneroEntity gFiccion = crearGenero("Ciencia Ficción");
        GeneroEntity gCiencia = crearGenero("Ciencia");
        GeneroEntity gFilosofia = crearGenero("Filosofía");
        GeneroEntity gNovela = crearGenero("Novela Latinoamericana");

        // 3. Autores (15 autores >= 12)
        AutorEntity aMartin = crearAutor("Robert C. Martin");
        AutorEntity aThomas = crearAutor("David Thomas");
        AutorEntity aHunt = crearAutor("Andrew Hunt");
        AutorEntity aHarari = crearAutor("Yuval Noah Harari");
        AutorEntity aHerbert = crearAutor("Frank Herbert");
        AutorEntity aSagan = crearAutor("Carl Sagan");
        AutorEntity aMarcoAurelio = crearAutor("Marco Aurelio");
        AutorEntity aGabo = crearAutor("Gabriel García Márquez");
        AutorEntity aBorges = crearAutor("Jorge Luis Borges");
        AutorEntity aCortazar = crearAutor("Julio Cortázar");
        AutorEntity aAllende = crearAutor("Isabel Allende");
        AutorEntity aRestrepo = crearAutor("Laura Restrepo");
        AutorEntity aAbad = crearAutor("Héctor Abad Faciolince");
        AutorEntity aFowler = crearAutor("Martin Fowler");
        AutorEntity aEvans = crearAutor("Eric Evans");

        // 4. Libros (16 libros: incluye los 6 del front + 10 clásicos/técnicos; 2 con año null; 1-5 ejemplares)
        crearLibroConEjemplares("978-0132350884", "Clean Code: A Handbook of Agile Software Craftsmanship",
                gTecnologia, List.of(aMartin), 2008, 3);
        crearLibroConEjemplares("978-0135957059", "The Pragmatic Programmer: Your Journey to Mastery",
                gTecnologia, List.of(aThomas, aHunt), 2019, 2);
        crearLibroConEjemplares("978-8499926223", "Sapiens: De animales a dioses",
                gHistoria, List.of(aHarari), 2014, 4);
        crearLibroConEjemplares("978-0441172719", "Dune",
                gFiccion, List.of(aHerbert), 1965, 5);
        crearLibroConEjemplares("978-0345331359", "Cosmos",
                gCiencia, List.of(aSagan), 1980, 1);
        crearLibroConEjemplares("978-8424937072", "Meditaciones",
                gFilosofia, List.of(aMarcoAurelio), 180, 4);

        crearLibroConEjemplares("978-0307474728", "Cien Años de Soledad",
                gNovela, List.of(aGabo), 1967, 3);
        crearLibroConEjemplares("978-0307387264", "El amor en los tiempos del cólera",
                gNovela, List.of(aGabo), 1985, 2);
        crearLibroConEjemplares("978-8420674209", "Ficciones",
                gNovela, List.of(aBorges), 1944, 2);
        crearLibroConEjemplares("978-8420633114", "El Aleph",
                gNovela, List.of(aBorges), 1949, 1);
        crearLibroConEjemplares("978-8437604572", "Rayuela",
                gNovela, List.of(aCortazar), 1963, 2);
        crearLibroConEjemplares("978-8401352836", "La casa de los espíritus",
                gNovela, List.of(aAllende), 1982, 3);
        crearLibroConEjemplares("978-9587041453", "Delirio",
                gNovela, List.of(aRestrepo), 2004, 1);
        crearLibroConEjemplares("978-9584215017", "El olvido que seremos",
                gHistoria, List.of(aAbad), 2006, 2);

        // 2 libros sin año de publicación (null) para verificar filtrado y presentación de «—»
        crearLibroConEjemplares("978-0134757599", "Refactoring: Improving the Design of Existing Code",
                gTecnologia, List.of(aFowler), null, 2);
        crearLibroConEjemplares("978-0321125217", "Domain-Driven Design: Tackling Complexity in the Heart of Software",
                gTecnologia, List.of(aEvans), null, 1);

        log.info("Datos de demostración sembrados exitosamente: 6 géneros, 15 autores, 16 libros.");
    }

    private GeneroEntity crearGenero(String nombre) {
        GeneroEntity g = new GeneroEntity();
        g.setNombreGenero(nombre);
        return generoRepository.save(g);
    }

    private AutorEntity crearAutor(String nombre) {
        AutorEntity a = new AutorEntity();
        a.setNombreAutor(nombre);
        return autorRepository.save(a);
    }

    private void crearLibroConEjemplares(
            String isbn,
            String titulo,
            GeneroEntity genero,
            List<AutorEntity> autores,
            Integer anioPublicacion,
            int cantidadEjemplares) {
        LibroEntity libro = new LibroEntity();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setGenero(genero);
        Set<AutorEntity> setAutores = new LinkedHashSet<>(autores);
        libro.setAutores(setAutores);
        libro.setAnioPublicacion(anioPublicacion);
        libro.setEstado(true);
        LibroEntity guardado = libroRepository.save(libro);

        List<EjemplarEntity> ejemplares = new ArrayList<>(cantidadEjemplares);
        for (int i = 0; i < cantidadEjemplares; i++) {
            EjemplarEntity ej = new EjemplarEntity();
            ej.setLibro(guardado);
            ej.setEstadoDisponibilidad(EjemplarEntity.DISPONIBLE);
            ejemplares.add(ej);
        }
        ejemplarRepository.saveAll(ejemplares);
    }
}
