package com.eam.biblioteca.persistenceLayer.mapper;

import com.eam.biblioteca.businessLayer.dto.usuario.UsuarioDTO;
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
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDTO toDTO(UsuarioEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setIdUsuario( entity.getIdUsuario() );
        usuarioDTO.setDocumentoIdentidad( entity.getDocumentoIdentidad() );
        usuarioDTO.setNombreCompleto( entity.getNombreCompleto() );
        usuarioDTO.setCorreo( entity.getCorreo() );
        usuarioDTO.setRol( entity.getRol() );
        usuarioDTO.setEstado( entity.getEstado() );

        return usuarioDTO;
    }

    @Override
    public List<UsuarioDTO> toDTOList(List<UsuarioEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioDTO> list = new ArrayList<UsuarioDTO>( entities.size() );
        for ( UsuarioEntity usuarioEntity : entities ) {
            list.add( toDTO( usuarioEntity ) );
        }

        return list;
    }
}
