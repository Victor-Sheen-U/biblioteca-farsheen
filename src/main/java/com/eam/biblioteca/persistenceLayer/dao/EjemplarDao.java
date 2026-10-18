package com.eam.biblioteca.persistenceLayer.dao;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.mapper.EjemplarMapper;
import com.eam.biblioteca.persistenceLayer.repository.EjemplarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EjemplarDao {

    private final EjemplarRepository ejemplarRepository;
    private final EjemplarMapper ejemplarMapper;

    public List<EjemplarDTO> findByLibroId(Long idLibro) {
        return ejemplarMapper.toDTOList(ejemplarRepository.findByLibroIdLibro(idLibro));
    }

    public List<EjemplarDTO> saveAll(List<EjemplarEntity> entities) {
        List<EjemplarEntity> guardados = ejemplarRepository.saveAll(entities);
        return ejemplarMapper.toDTOList(guardados);
    }

    public List<EjemplarEntity> findDisponiblesEnRango(Long idLibro, LocalDateTime inicio, LocalDateTime fin) {
        return ejemplarRepository.findDisponiblesEnRango(idLibro, inicio, fin);
    }

    public int contarDisponiblesEnInstante(Long idLibro, LocalDateTime instante) {
        return ejemplarRepository.findDisponiblesEnInstante(idLibro, instante).size();
    }
}
