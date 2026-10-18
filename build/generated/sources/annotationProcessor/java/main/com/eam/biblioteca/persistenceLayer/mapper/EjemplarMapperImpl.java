package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
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
public class EjemplarMapperImpl implements EjemplarMapper {

    @Override
    public EjemplarDTO toDTO(EjemplarEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EjemplarDTO ejemplarDTO = new EjemplarDTO();

        ejemplarDTO.setIdLibro( entityLibroIdLibro( entity ) );
        ejemplarDTO.setIdEjemplar( entity.getIdEjemplar() );
        ejemplarDTO.setEstadoDisponibilidad( entity.getEstadoDisponibilidad() );

        return ejemplarDTO;
    }

    @Override
    public List<EjemplarDTO> toDTOList(List<EjemplarEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EjemplarDTO> list = new ArrayList<EjemplarDTO>( entities.size() );
        for ( EjemplarEntity ejemplarEntity : entities ) {
            list.add( toDTO( ejemplarEntity ) );
        }

        return list;
    }

    private Long entityLibroIdLibro(EjemplarEntity ejemplarEntity) {
        LibroEntity libro = ejemplarEntity.getLibro();
        if ( libro == null ) {
            return null;
        }
        return libro.getIdLibro();
    }
}
