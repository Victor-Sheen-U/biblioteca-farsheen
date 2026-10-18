package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    @Query("SELECT r FROM ReservaEntity r WHERE r.usuario.idUsuario = :idUsuario ORDER BY r.fechaInicio DESC")
    List<ReservaEntity> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT r FROM ReservaEntity r WHERE r.ejemplar.libro.idLibro = :idLibro ORDER BY r.fechaInicio DESC")
    List<ReservaEntity> findByLibroId(@Param("idLibro") Long idLibro);
}
