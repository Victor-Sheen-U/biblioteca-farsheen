package com.eam.biblioteca.businessLayer.service;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaCreateDTO;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;

import java.util.List;

public interface ReservaService {

    ReservaDTO crear(String correo, ReservaCreateDTO dto);

    ReservaDTO buscarPorId(Long id, String correo, boolean esAdmin);

    List<ReservaDTO> listarTodas();

    List<ReservaDTO> listarPorUsuario(Long idUsuario);

    List<ReservaDTO> listarMisReservas(String correo);

    ReservaDTO cancelarComoUsuario(Long id, String correo);

    ReservaDTO cancelarComoAdmin(Long id);
}
