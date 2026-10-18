package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.businessLayer.dto.libro.LibroDTO;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-18T16:26:39-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.7.1.jar, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class LibroMapperImpl implements LibroMapper {

    @Override
    public LibroDTO toDTO(LibroEntity entity) {
        if ( entity == null ) {
            return null;
        }

        LibroDTO libroDTO = new LibroDTO();

        libroDTO.setIdGenero( entityGeneroIdGenero( entity ) );
        libroDTO.setNombreGenero( entityGeneroNombreGenero( entity ) );
        libroDTO.setIdLibro( entity.getIdLibro() );
        libroDTO.setIsbn( entity.getIsbn() );
        libroDTO.setTitulo( entity.getTitulo() );
        libroDTO.setAutores( autorEntitySetToAutorDTOList( entity.getAutores() ) );
        libroDTO.setEstado( entity.getEstado() );

        return libroDTO;
    }

    @Override
    public List<LibroDTO> toDTOList(List<LibroEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<LibroDTO> list = new ArrayList<LibroDTO>( entities.size() );
        for ( LibroEntity libroEntity : entities ) {
            list.add( toDTO( libroEntity ) );
        }

        return list;
    }

    private Long entityGeneroIdGenero(LibroEntity libroEntity) {
        GeneroEntity genero = libroEntity.getGenero();
        if ( genero == null ) {
            return null;
        }
        return genero.getIdGenero();
    }

    private String entityGeneroNombreGenero(LibroEntity libroEntity) {
        GeneroEntity genero = libroEntity.getGenero();
        if ( genero == null ) {
            return null;
        }
        return genero.getNombreGenero();
    }

    protected AutorDTO autorEntityToAutorDTO(AutorEntity autorEntity) {
        if ( autorEntity == null ) {
            return null;
        }

        AutorDTO autorDTO = new AutorDTO();

        autorDTO.setIdAutor( autorEntity.getIdAutor() );
        autorDTO.setNombreAutor( autorEntity.getNombreAutor() );

        return autorDTO;
    }

    protected List<AutorDTO> autorEntitySetToAutorDTOList(Set<AutorEntity> set) {
        if ( set == null ) {
            return null;
        }

        List<AutorDTO> list = new ArrayList<AutorDTO>( set.size() );
        for ( AutorEntity autorEntity : set ) {
            list.add( autorEntityToAutorDTO( autorEntity ) );
        }

        return list;
    }
}
