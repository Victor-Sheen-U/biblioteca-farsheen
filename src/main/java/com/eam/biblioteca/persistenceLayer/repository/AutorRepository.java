package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {

    Optional<AutorEntity> findByNombreAutorIgnoreCase(String nombreAutor);
}
