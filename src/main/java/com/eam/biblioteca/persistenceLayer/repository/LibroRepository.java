package com.eam.biblioteca.persistenceLayer.repository;

import com.eam.biblioteca.persistenceLayer.entity.LibroEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, Long> {

    @EntityGraph(attributePaths = {"genero", "autores"})
    @Query("SELECT DISTINCT l FROM LibroEntity l WHERE l.estado = true ORDER BY l.titulo ASC")
    List<LibroEntity> findAllHabilitadosConRelaciones();

    @EntityGraph(attributePaths = {"genero", "autores"})
    @Query("SELECT l FROM LibroEntity l WHERE l.idLibro = :id AND l.estado = true")
    Optional<LibroEntity> findHabilitadoByIdConRelaciones(@Param("id") Long id);

    @Query("SELECT l FROM LibroEntity l WHERE l.estado = true AND LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) ORDER BY l.titulo")
    List<LibroEntity> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT l FROM LibroEntity l WHERE l.estado = true AND LOWER(l.genero.nombreGenero) LIKE LOWER(CONCAT('%', :genero, '%')) ORDER BY l.titulo")
    List<LibroEntity> buscarPorGenero(@Param("genero") String genero);

    @Query("SELECT DISTINCT l FROM LibroEntity l JOIN l.autores a WHERE l.estado = true AND LOWER(a.nombreAutor) LIKE LOWER(CONCAT('%', :autor, '%')) ORDER BY l.titulo")
    List<LibroEntity> buscarPorAutor(@Param("autor") String autor);
}
