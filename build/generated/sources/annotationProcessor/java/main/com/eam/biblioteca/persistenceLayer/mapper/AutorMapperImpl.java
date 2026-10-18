package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
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
public class AutorMapperImpl implements AutorMapper {

    @Override
    public AutorDTO toDTO(AutorEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AutorDTO autorDTO = new AutorDTO();

        autorDTO.setIdAutor( entity.getIdAutor() );
        autorDTO.setNombreAutor( entity.getNombreAutor() );

        return autorDTO;
    }

    @Override
    public List<AutorDTO> toDTOList(List<AutorEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AutorDTO> list = new ArrayList<AutorDTO>( entities.size() );
        for ( AutorEntity autorEntity : entities ) {
            list.add( toDTO( autorEntity ) );
        }

        return list;
    }
}
