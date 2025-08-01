package com.aluracursos.screenmatch.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String title;

    private Integer cantidadTemporadas;
    private Double rate;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String sinopsis;
    private String actores;
    private String poster;

    @OneToMany(mappedBy = "serie",  cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios;

    public Serie() {}

    public Serie(DatosSerie datosSerie) {
        this.title = datosSerie.title();
        this.cantidadTemporadas = datosSerie.cantidadTemporadas();
        this.rate = OptionalDouble.of(Double.valueOf(datosSerie.rate())).orElse(0);
        this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
        this.sinopsis = datosSerie.sinopsis(); //aca podria ir lo de la api de chatgpt si pudiera usarla :(
        this.actores = datosSerie.actores();
        this.poster = datosSerie.poster();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCantidadTemporadas() {
        return cantidadTemporadas;
    }

    public void setCantidadTemporadas(Integer cantidadTemporadas) {
        this.cantidadTemporadas = cantidadTemporadas;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return "genero=" + genero +
                ", title=" + title +
                ", cantidadTemporadas=" + cantidadTemporadas +
                ", rate=" + rate +
                ", sinopsis='" + sinopsis +
                ", actores='" + actores +
                ", poster='" + poster +
                ", episodios='" + episodios;
    }
}
