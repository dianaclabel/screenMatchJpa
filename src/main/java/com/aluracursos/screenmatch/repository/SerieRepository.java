package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Es una interfaz que permite manejar datos de una base de datos en Spring Data.

public interface SerieRepository extends JpaRepository <Serie,Long> {

    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();
    List <Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer totalTemporadas, double evaluacion);
    //@Query( value ="SELECT * FROM series WHERE series.total_temporadas <= 6 AND series.evaluacion >=7.5, nativeQuery = true", nativeQuery = true)
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion ")
    List<Serie> seriesPorTemporadaYEvaluacion(int totalTemporadas, Double evaluacion);

}
