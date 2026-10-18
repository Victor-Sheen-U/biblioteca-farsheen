package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombreCompleto", target = "nombreUsuario")
    @Mapping(source = "ejemplar.idEjemplar", target = "idEjemplar")
    @Mapping(source = "ejemplar.libro.idLibro", target = "idLibro")
    @Mapping(source = "ejemplar.libro.titulo", target = "tituloLibro")
    @Mapping(target = "estadoReserva", expression = "java(entity.calcularEstadoEfectivo())")
    ReservaDTO toDTO(ReservaEntity entity);

    List<ReservaDTO> toDTOList(List<ReservaEntity> entities);
}
