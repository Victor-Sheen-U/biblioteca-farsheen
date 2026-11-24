package com.eam.biblioteca.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long idLibro;

    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero", nullable = false)
    private GeneroEntity genero;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    @OrderBy("nombreAutor ASC")
    private Set<AutorEntity> autores = new HashSet<>();

    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    public LibroEntity(Long idLibro, String isbn, String titulo, GeneroEntity genero, Set<AutorEntity> autores, Boolean estado) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.genero = genero;
        this.autores = autores;
        this.estado = estado;
    }
}
