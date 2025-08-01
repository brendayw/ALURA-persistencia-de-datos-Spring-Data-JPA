package com.aluracursos.screenmatch.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
        @JsonAlias("Title") String title,
        @JsonAlias("totalSeasons") Integer cantidadTemporadas,
        @JsonAlias("imdbRating") String rate,
        @JsonAlias("Genre") String genero,
        @JsonAlias("Plot") String sinopsis,
        @JsonAlias("Actors") String actores,
        @JsonAlias("Poster") String poster) {
}
