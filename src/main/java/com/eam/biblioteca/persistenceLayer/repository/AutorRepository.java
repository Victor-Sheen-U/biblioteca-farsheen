package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {
}
