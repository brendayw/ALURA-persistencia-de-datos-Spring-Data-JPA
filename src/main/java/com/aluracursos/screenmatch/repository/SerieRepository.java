package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.models.Categoria;
import com.aluracursos.screenmatch.models.Episodio;
import com.aluracursos.screenmatch.models.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTitleContainsIgnoreCase(String nombreSerie);
    List<Serie> findTop5ByOrderByRateDesc();
    List<Serie> findByGenero(Categoria categoria);

    @Query("SELECT s FROM Serie s WHERE s.cantidadTemporadas <= :cantidadTemporadas AND s.rate >= :rate")
    List<Serie> seriesPorTemporadaYRate(int cantidadTemporadas, Double rate);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.rate DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);
}
