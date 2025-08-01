package com.aluracursos.screenmatch.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosEpisodio(
    @JsonAlias("Title") String title,
    @JsonAlias("Episode") Integer numeroEpisodio,
    @JsonAlias("imdbRating") String rate,
    @JsonAlias("Released") String fechaLanzamiento
) {
}
