package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.autor.AutorDTO;
import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    AutorDTO toDTO(AutorEntity entity);

    List<AutorDTO> toDTOList(List<AutorEntity> entities);
}
