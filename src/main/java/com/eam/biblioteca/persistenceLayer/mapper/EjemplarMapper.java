package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.ejemplar.EjemplarDTO;
import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EjemplarMapper {

    @Mapping(source = "libro.idLibro", target = "idLibro")
    EjemplarDTO toDTO(EjemplarEntity entity);

    List<EjemplarDTO> toDTOList(List<EjemplarEntity> entities);
}
