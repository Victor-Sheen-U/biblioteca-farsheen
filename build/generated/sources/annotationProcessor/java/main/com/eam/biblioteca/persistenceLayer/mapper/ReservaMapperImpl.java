package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import com.eam.biblioteca.persistenceLayer.entity.UsuarioEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-18T16:26:39-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.7.1.jar, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ReservaMapperImpl implements ReservaMapper {

    @Override
    public ReservaDTO toDTO(ReservaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReservaDTO reservaDTO = new ReservaDTO();

        reservaDTO.setIdUsuario( entityUsuarioIdUsuario( entity ) );
        reservaDTO.setNombreUsuario( entityUsuarioNombreCompleto( entity ) );
        reservaDTO.setIdEjemplar( entityEjemplarIdEjemplar( entity ) );
        reservaDTO.setIdLibro( entityEjemplarLibroIdLibro( entity ) );
        reservaDTO.setTituloLibro( entityEjemplarLibroTitulo( entity ) );
        reservaDTO.setIdReserva( entity.getIdReserva() );
        reservaDTO.setFechaSolicitud( entity.getFechaSolicitud() );
        reservaDTO.setFechaInicio( entity.getFechaInicio() );
        reservaDTO.setFechaEntrega( entity.getFechaEntrega() );

        reservaDTO.setEstadoReserva( entity.calcularEstadoEfectivo() );

        return reservaDTO;
    }

    @Override
    public List<ReservaDTO> toDTOList(List<ReservaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReservaDTO> list = new ArrayList<ReservaDTO>( entities.size() );
        for ( ReservaEntity reservaEntity : entities ) {
            list.add( toDTO( reservaEntity ) );
        }

        return list;
    }

    private Long entityUsuarioIdUsuario(ReservaEntity reservaEntity) {
        UsuarioEntity usuario = reservaEntity.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getIdUsuario();
    }

    private String entityUsuarioNombreCompleto(ReservaEntity reservaEntity) {
        UsuarioEntity usuario = reservaEntity.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombreCompleto();
    }

    private Long entityEjemplarIdEjemplar(ReservaEntity reservaEntity) {
        EjemplarEntity ejemplar = reservaEntity.getEjemplar();
        if ( ejemplar == null ) {
            return null;
        }
        return ejemplar.getIdEjemplar();
    }

    private Long entityEjemplarLibroIdLibro(ReservaEntity reservaEntity) {
        EjemplarEntity ejemplar = reservaEntity.getEjemplar();
        if ( ejemplar == null ) {
            return null;
        }
        LibroEntity libro = ejemplar.getLibro();
        if ( libro == null ) {
            return null;
        }
        return libro.getIdLibro();
    }

    private String entityEjemplarLibroTitulo(ReservaEntity reservaEntity) {
        EjemplarEntity ejemplar = reservaEntity.getEjemplar();
        if ( ejemplar == null ) {
            return null;
        }
        LibroEntity libro = ejemplar.getLibro();
        if ( libro == null ) {
            return null;
        }
        return libro.getTitulo();
    }
}
