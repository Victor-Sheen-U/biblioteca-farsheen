package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaCreateDTO;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.businessLayer.service.ReservaService;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.dao.ReservaDao;
import com.eam.biblioteca.persistenceLayer.dao.UsuarioDao;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import com.eam.biblioteca.presentationLayer.exception.BadRequestException;
import com.eam.biblioteca.presentationLayer.exception.ForbiddenException;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaDao reservaDao;
    private final UsuarioDao usuarioDao;
    private final LibroDao libroDao;
    private final EjemplarDao ejemplarDao;

    @Override
    @Transactional
    public ReservaDTO crear(String correo, ReservaCreateDTO dto) {
        String correoNorm = correo.trim().toLowerCase();
        UsuarioEntity usuario = usuarioDao.findEntityByCorreo(correoNorm)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + correoNorm));

        LibroEntity libro = libroDao.findEntityById(dto.getIdLibro())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + dto.getIdLibro()));

        if (Boolean.FALSE.equals(libro.getEstado())) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + dto.getIdLibro());
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (!dto.getFechaInicio().isAfter(ahora)) {
            throw new BadRequestException("La fecha de inicio debe ser futura");
        }

        if (!dto.getFechaEntrega().isAfter(dto.getFechaInicio())) {
            throw new BadRequestException("La fecha de entrega debe ser posterior a la fecha de inicio");
        }

        List<EjemplarEntity> disponibles = ejemplarDao.findDisponiblesEnRango(
                dto.getIdLibro(), dto.getFechaInicio(), dto.getFechaEntrega());

        if (disponibles.isEmpty()) {
            throw new BadRequestException("No hay ejemplares disponibles para el rango de fechas solicitado");
        }

        EjemplarEntity ejemplarAsignado = disponibles.get(0);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(usuario);
        reserva.setEjemplar(ejemplarAsignado);
        reserva.setFechaSolicitud(ahora);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaEntrega(dto.getFechaEntrega());
        reserva.setEstadoReserva(ReservaEntity.ACTIVA);

        return reservaDao.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaDTO buscarPorId(Long id, String correo, boolean esAdmin) {
        ReservaDTO reserva = reservaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        if (!esAdmin) {
            String correoNorm = correo.trim().toLowerCase();
            UsuarioEntity usuario = usuarioDao.findEntityByCorreo(correoNorm)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + correoNorm));

            if (!reserva.getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new ForbiddenException("No tiene permiso para consultar esta reserva");
            }
        }

        return reserva;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarTodas() {
        return reservaDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarPorUsuario(Long idUsuario) {
        if (usuarioDao.findEntityById(idUsuario).isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        return reservaDao.findByUsuarioId(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarMisReservas(String correo) {
        String correoNorm = correo.trim().toLowerCase();
        UsuarioEntity usuario = usuarioDao.findEntityByCorreo(correoNorm)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + correoNorm));

        return reservaDao.findByUsuarioId(usuario.getIdUsuario());
    }

    @Override
    @Transactional
    public ReservaDTO cancelarComoUsuario(Long id, String correo) {
        ReservaEntity reserva = reservaDao.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        String correoNorm = correo.trim().toLowerCase();
        if (!reserva.getUsuario().getCorreo().equalsIgnoreCase(correoNorm)) {
            throw new ForbiddenException("No puede cancelar una reserva que no le pertenece");
        }

        String estadoEfectivo = reserva.calcularEstadoEfectivo();
        if (ReservaEntity.CANCELADA.equals(estadoEfectivo)) {
            throw new BadRequestException("La reserva ya está cancelada");
        }
        if (ReservaEntity.FINALIZADA.equals(estadoEfectivo)) {
            throw new BadRequestException("No se puede cancelar una reserva finalizada");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = reserva.getFechaInicio().minusHours(24);
        if (ahora.isAfter(limite)) {
            throw new BadRequestException("Solo se puede cancelar hasta 24 horas antes de la fecha de inicio");
        }

        reserva.setEstadoReserva(ReservaEntity.CANCELADA);
        return reservaDao.save(reserva);
    }

    @Override
    @Transactional
    public ReservaDTO cancelarComoAdmin(Long id) {
        ReservaEntity reserva = reservaDao.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        if (ReservaEntity.CANCELADA.equals(reserva.getEstadoReserva())) {
            throw new BadRequestException("La reserva ya está cancelada");
        }

        reserva.setEstadoReserva(ReservaEntity.CANCELADA);
        return reservaDao.save(reserva);
    }
}
