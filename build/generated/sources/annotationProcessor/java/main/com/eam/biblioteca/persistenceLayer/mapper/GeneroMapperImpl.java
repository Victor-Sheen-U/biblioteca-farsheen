package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
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
public class GeneroMapperImpl implements GeneroMapper {

    @Override
    public GeneroDTO toDTO(GeneroEntity entity) {
        if ( entity == null ) {
            return null;
        }

        GeneroDTO generoDTO = new GeneroDTO();

        generoDTO.setIdGenero( entity.getIdGenero() );
        generoDTO.setNombreGenero( entity.getNombreGenero() );

        return generoDTO;
    }

    @Override
    public List<GeneroDTO> toDTOList(List<GeneroEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<GeneroDTO> list = new ArrayList<GeneroDTO>( entities.size() );
        for ( GeneroEntity generoEntity : entities ) {
            list.add( toDTO( generoEntity ) );
        }

        return list;
    }
}
