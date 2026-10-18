package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import com.eam.biblioteca.persistenceLayer.mapper.ReservaMapper;
import com.eam.biblioteca.persistenceLayer.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservaDao {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;

    public Optional<ReservaDTO> findById(Long id) {
        return reservaRepository.findById(id).map(reservaMapper::toDTO);
    }

    public Optional<ReservaEntity> findEntityById(Long id) {
        return reservaRepository.findById(id);
    }

    public List<ReservaDTO> findAll() {
        return reservaMapper.toDTOList(reservaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaInicio")));
    }

    public List<ReservaDTO> findByUsuarioId(Long idUsuario) {
        return reservaMapper.toDTOList(reservaRepository.findByUsuarioId(idUsuario));
    }

    public List<ReservaDTO> findByLibroId(Long idLibro) {
        return reservaMapper.toDTOList(reservaRepository.findByLibroId(idLibro));
    }

    public ReservaDTO save(ReservaEntity entity) {
        ReservaEntity guardado = reservaRepository.save(entity);
        return reservaMapper.toDTO(guardado);
    }
}
