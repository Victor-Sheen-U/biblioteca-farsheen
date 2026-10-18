package com.eam.biblioteca.businessLayer.service.impl;

import com.eam.biblioteca.businessLayer.dto.libro.DisponibilidadDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroCreateDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroDetalleDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroUpdateDTO;
import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.businessLayer.service.LibroService;
import com.eam.biblioteca.persistenceLayer.dao.AutorDao;
import com.eam.biblioteca.persistenceLayer.dao.EjemplarDao;
import com.eam.biblioteca.persistenceLayer.dao.GeneroDao;
import com.eam.biblioteca.persistenceLayer.dao.LibroDao;
import com.eam.biblioteca.persistenceLayer.dao.ReservaDao;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.presentationLayer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroDao libroDao;
    private final GeneroDao generoDao;
    private final AutorDao autorDao;
    private final EjemplarDao ejemplarDao;
    private final ReservaDao reservaDao;

    @Override
    @Transactional
    public LibroDTO crear(LibroCreateDTO dto) {
        GeneroEntity genero = generoDao.findEntityById(dto.getIdGenero())
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + dto.getIdGenero()));

        List<AutorEntity> autoresList = autorDao.findEntitiesByIds(dto.getIdsAutores());
        validarAutores(dto.getIdsAutores(), autoresList);

        LibroEntity libro = new LibroEntity();
        libro.setIsbn(dto.getIsbn().trim());
        libro.setTitulo(dto.getTitulo().trim());
        libro.setGenero(genero);
        libro.setAutores(new HashSet<>(autoresList));
        libro.setEstado(true);

        return libroDao.save(libro);
    }

    @Override
    @Transactional
    public LibroDTO actualizar(Long id, LibroUpdateDTO dto) {
        LibroEntity libro = libroDao.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        GeneroEntity genero = generoDao.findEntityById(dto.getIdGenero())
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + dto.getIdGenero()));

        List<AutorEntity> autoresList = autorDao.findEntitiesByIds(dto.getIdsAutores());
        validarAutores(dto.getIdsAutores(), autoresList);

        libro.setIsbn(dto.getIsbn().trim());
        libro.setTitulo(dto.getTitulo().trim());
        libro.setGenero(genero);
        libro.setAutores(new HashSet<>(autoresList));

        return libroDao.save(libro);
    }

    @Override
    @Transactional
    public void inhabilitar(Long id) {
        LibroEntity libro = libroDao.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
        libro.setEstado(false);
        libroDao.save(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroDTO buscarPorId(Long id) {
        return libroDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> buscarPorTitulo(String titulo) {
        return libroDao.buscarPorTitulo(titulo != null ? titulo.trim() : "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> buscarPorGenero(String genero) {
        return libroDao.buscarPorGenero(genero != null ? genero.trim() : "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> buscarPorAutor(String autor) {
        return libroDao.buscarPorAutor(autor != null ? autor.trim() : "");
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadDTO consultarDisponibilidad(Long idLibro, LocalDateTime fechaHora) {
        LibroEntity libro = libroDao.findEntityById(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro));

        if (Boolean.FALSE.equals(libro.getEstado())) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro);
        }

        int count = ejemplarDao.contarDisponiblesEnInstante(idLibro, fechaHora);
        return new DisponibilidadDTO(idLibro, fechaHora, count > 0, count);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroDetalleDTO consultarDetalle(Long idLibro) {
        LibroDTO libroDTO = libroDao.findById(idLibro)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + idLibro));

        List<ReservaDTO> reservas = reservaDao.findByLibroId(idLibro);
        return new LibroDetalleDTO(libroDTO, reservas.size(), reservas);
    }

    private void validarAutores(List<Long> idsPedidos, List<AutorEntity> autoresEncontrados) {
        Set<Long> idsEncontrados = autoresEncontrados.stream()
                .map(AutorEntity::getIdAutor)
                .collect(Collectors.toSet());

        for (Long idPedido : idsPedidos) {
            if (!idsEncontrados.contains(idPedido)) {
                throw new ResourceNotFoundException("Autor no encontrado con ID: " + idPedido);
            }
        }
    }
}
