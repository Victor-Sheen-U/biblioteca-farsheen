package com.eam.biblioteca.persistenceLayer.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservaEntityTest {

    @Test
    @DisplayName("calcularEstadoEfectivo cuando estadoReserva es ACTIVA y fechaEntrega es futura retorna ACTIVA")
    void calcularEstadoEfectivo_ActivaYFutura_RetornaActiva() {
        ReservaEntity reserva = new ReservaEntity();
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(5));

        assertEquals(ReservaEntity.ACTIVA, reserva.calcularEstadoEfectivo());
    }

    @Test
    @DisplayName("calcularEstadoEfectivo cuando estadoReserva es ACTIVA pero fechaEntrega es pasada retorna FINALIZADA")
    void calcularEstadoEfectivo_ActivaYPasada_RetornaFinalizada() {
        ReservaEntity reserva = new ReservaEntity();
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);
        reserva.setFechaEntrega(LocalDateTime.now().minusDays(1));

        assertEquals(ReservaEntity.FINALIZADA, reserva.calcularEstadoEfectivo());
    }

    @Test
    @DisplayName("calcularEstadoEfectivo cuando estadoReserva es CANCELADA retorna CANCELADA sin importar fecha")
    void calcularEstadoEfectivo_Cancelada_RetornaCancelada() {
        ReservaEntity reserva = new ReservaEntity();
        reserva.setEstadoReserva(ReservaEntity.CANCELADA);
        reserva.setFechaEntrega(LocalDateTime.now().plusDays(5));

        assertEquals(ReservaEntity.CANCELADA, reserva.calcularEstadoEfectivo());
    }
}
