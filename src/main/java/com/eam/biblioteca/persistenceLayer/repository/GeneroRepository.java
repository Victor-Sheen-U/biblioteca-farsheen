package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.GeneroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {

    boolean existsByNombreGeneroIgnoreCase(String nombreGenero);

    Optional<GeneroEntity> findByNombreGeneroIgnoreCase(String nombreGenero);
}
