package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Es una interfaz que permite manejar datos de una base de datos en Spring Data.

public interface SerieRepository extends JpaRepository <Serie,Long> {

    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();
    List <Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer totalTemporadas, double evaluacion);


}
