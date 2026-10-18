package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.genero.GeneroDTO;
import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeneroMapper {

    GeneroDTO toDTO(GeneroEntity entity);

    List<GeneroDTO> toDTOList(List<GeneroEntity> entities);
}
