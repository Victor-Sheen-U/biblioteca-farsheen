package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.EjemplarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EjemplarRepository extends JpaRepository<EjemplarEntity, Long> {

    @Query("SELECT e FROM EjemplarEntity e WHERE e.libro.idLibro = :idLibro ORDER BY e.idEjemplar ASC")
    List<EjemplarEntity> findByLibroIdLibro(@Param("idLibro") Long idLibro);

    @Query("""
        SELECT e FROM EjemplarEntity e
        WHERE e.libro.idLibro = :idLibro
          AND e.estadoDisponibilidad = 'DISPONIBLE'
          AND NOT EXISTS (
              SELECT r.idReserva FROM ReservaEntity r
              WHERE r.ejemplar = e AND r.estadoReserva = 'ACTIVA'
                AND r.fechaInicio < :fin AND r.fechaEntrega > :inicio)
        ORDER BY e.idEjemplar ASC""")
    List<EjemplarEntity> findDisponiblesEnRango(@Param("idLibro") Long idLibro,
            @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT e FROM EjemplarEntity e
        WHERE e.libro.idLibro = :idLibro
          AND e.estadoDisponibilidad = 'DISPONIBLE'
          AND NOT EXISTS (
              SELECT r.idReserva FROM ReservaEntity r
              WHERE r.ejemplar = e AND r.estadoReserva = 'ACTIVA'
                AND r.fechaInicio <= :instante AND r.fechaEntrega > :instante)
        ORDER BY e.idEjemplar ASC""")
    List<EjemplarEntity> findDisponiblesEnInstante(@Param("idLibro") Long idLibro,
            @Param("instante") LocalDateTime instante);

    @Query("SELECT COUNT(e.idEjemplar) FROM EjemplarEntity e WHERE e.libro.idLibro = :idLibro")
    long countByLibroIdLibro(@Param("idLibro") Long idLibro);

    @Query("""
        SELECT e.libro.idLibro, COUNT(e.idEjemplar)
        FROM EjemplarEntity e
        WHERE e.libro.estado = true
        GROUP BY e.libro.idLibro""")
    List<Object[]> contarTotalPorLibroHabilitado();

    @Query("""
        SELECT e.libro.idLibro, COUNT(e.idEjemplar)
        FROM EjemplarEntity e
        WHERE e.libro.estado = true
          AND e.estadoDisponibilidad = 'DISPONIBLE'
          AND NOT EXISTS (
              SELECT r.idReserva FROM ReservaEntity r
              WHERE r.ejemplar = e AND r.estadoReserva = 'ACTIVA'
                AND r.fechaInicio <= :instante AND r.fechaEntrega > :instante)
        GROUP BY e.libro.idLibro""")
    List<Object[]> contarDisponiblesEnInstantePorLibroHabilitado(@Param("instante") LocalDateTime instante);
}
