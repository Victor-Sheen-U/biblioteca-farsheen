package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.libro.LibroDTO;
import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LibroMapper {

    @Mapping(source = "genero.idGenero", target = "idGenero")
    @Mapping(source = "genero.nombreGenero", target = "nombreGenero")
    LibroDTO toDTO(LibroEntity entity);

    List<LibroDTO> toDTOList(List<LibroEntity> entities);
}
