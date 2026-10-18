package com.eam.biblioteca.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ejemplar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EjemplarEntity {

    public static final String DISPONIBLE = "DISPONIBLE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar")
    private Long idEjemplar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private LibroEntity libro;

    @Column(name = "estado_disponibilidad", nullable = false, length = 20)
    private String estadoDisponibilidad = DISPONIBLE;
}
