package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarCreateDTO;
import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.businessLayer.service.EjemplarService;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EjemplarServiceImpl implements EjemplarService {

    private final EjemplarDao ejemplarDao;
    private final LibroDao libroDao;

    @Override
    @Transactional
    public List<EjemplarDTO> crear(Long idLibro, EjemplarCreateDTO dto) {
        LibroEntity libro = libroDao.findEntityById(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro));

        int cantidad = (dto != null && dto.getCantidad() != null) ? dto.getCantidad() : 1;
        List<EjemplarEntity> ejemplares = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            EjemplarEntity ejemplar = new EjemplarEntity();
            ejemplar.setLibro(libro);
            ejemplar.setEstadoDisponibilidad(EjemplarEntity.DISPONIBLE);
            ejemplares.add(ejemplar);
        }

        return ejemplarDao.saveAll(ejemplares);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarDTO> listarPorLibro(Long idLibro) {
        if (libroDao.findEntityById(idLibro).isEmpty()) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro);
        }
        return ejemplarDao.findByLibroId(idLibro);
    }
}
