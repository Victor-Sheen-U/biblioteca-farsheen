package com.eam.biblioteca.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {

    public static final String ACTIVA = "ACTIVA";
    public static final String CANCELADA = "CANCELADA";
    public static final String FINALIZADA = "FINALIZADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejemplar", nullable = false)
    private EjemplarEntity ejemplar;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDateTime fechaEntrega;

    @Column(name = "estado_reserva", nullable = false, length = 20)
    private String estadoReserva;

    public String calcularEstadoEfectivo() {
        if (CANCELADA.equals(estadoReserva)) {
            return CANCELADA;
        }
        if (fechaEntrega != null && fechaEntrega.isBefore(LocalDateTime.now())) {
            return FINALIZADA;
        }
        return ACTIVA;
    }
}
