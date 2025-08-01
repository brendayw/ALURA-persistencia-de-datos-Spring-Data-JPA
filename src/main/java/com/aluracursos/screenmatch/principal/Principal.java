package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.models.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner input = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=379dea3";
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void mostrarMenu() {
        //arreglos
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria
                    7 - Filtrar series por temporadas y evaluación
                    8 - Buscar episodios por titulo
                    9 - Top 5 episodios por Serie
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = input.nextInt();
            input.nextLine(); //por si no lee el nextInt()

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5MejoresSeries();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYRate();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la app...");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribi el nombre de la serie a buscar: ");
        var nombreSerie = input.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        //DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.println("Escribi el nombre de la serie que quieres ver los episodios: ");
        var nombreSerie = input.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getCantidadTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitle().replace(" ", "+") + "&Season=" + i + API_KEY);
                DatosTemporada datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSerie.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribi el nombre de la serie que quieres buscar: ");
        var nombreSerie = input.nextLine();
        serieBuscada = repositorio.findByTitleContainsIgnoreCase(nombreSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }

    private void buscarTop5MejoresSeries() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByRateDesc();
        topSeries.forEach(s ->
                System.out.println("Serie: " + s.getTitle() + " Rate: " + s.getRate())
        );
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escribi la categoria de serie que queres buscar: ");
        var genero = input.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series encontradas: " + genero);
        seriePorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaYRate() {
        System.out.println("Filtrar series con cuantas temporadas?");
        var totalTemporadas = input.nextInt();
        input.nextLine();

        System.out.println("Cual deberia ser el valor de la calificacion (rate)");
        var calificacion = input.nextDouble();
        input.nextLine();

        List<Serie> filtradoSeries = repositorio.seriesPorTemporadaYRate(totalTemporadas, calificacion );
        System.out.println("Series filtradas: ");
        filtradoSeries.forEach(s -> System.out.println(s.getTitle() + " calificacion: " + s.getRate()));
    }

    private void buscarEpisodiosPorTitulo() {
        System.out.println("Escribi el nombre del episodio a buscar: ");
        var nombreEpisodio = input.nextLine();
        List<Episodio> episodioBuscado = repositorio.episodiosPorNombre(nombreEpisodio);
        episodioBuscado.forEach(e ->
                System.out.printf("Serie: %s | Temporada: %d | Episodio: %d | Calificación: %.2f%n",
                        e.getSerie().getTitle(), e.getTemporada(), e.getNumeroEpisodio(), e.getRate()));

    }

    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            if (topEpisodios.isEmpty()) {
                System.out.println("No se encontraron episodios en el top 5 para esta serie.");
            } else {
                topEpisodios.forEach(e ->
                        System.out.printf("Serie: %s | Temporada: %d | Episodio: %s | Evaluación: %.2f%n",
                                e.getSerie().getTitle(), e.getTemporada(), e.getTitulo(), e.getRate()));
            }
        } else {
            System.out.println("Serie no encontrada.");
        }
    }

        //mostrar solo el titulo de los episodios
//        for (int i = 0; i < datos.cantidadTemporadas(); i++) {
//            List<DatosEpisodio> listaEpisodios = temporadas.get(i).episodios();
//            for (int j = 0; j < listaEpisodios.size(); j++) {
//                System.out.println(listaEpisodios.get(j).title());
//            }
//        }

        //usando lambda
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println("Titulo del episodio: " + e.title() + ", Numero de episodio: " + e.numeroEpisodio())));

        //convertir a lista de datosEpisodio
//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());

        //top 5 episodios
//        System.out.println("Top 5 de episodios: ");
//        datosEpisodios.stream()
//                .filter(e -> !e.rate().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primer filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DatosEpisodio::rate).reversed())
//                .peek(e -> System.out.println("Segundo filtro ordena de M - m " + e))
//                .map(e -> e.title().toUpperCase())
//                .peek(e -> System.out.println("Tercer filtro todo a mayuscula " + e))
//                .limit(5)
//                .forEach(System.out::println);

//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d)))
//                .collect(Collectors.toList());
        //episodios.forEach(System.out::println);

        //buscar episodios a partir de cierto año
//        System.out.println("Indica el año a partir del cual deseas ver los episodios: ");
//        var fecha = input.nextInt();
//        input.nextLine();

//        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getFechaLanzamiento() != null && e.getFechaLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Epidosido: " + e.getTitulo() +
//                                " Fecha de Lanzamiento: " + e.getFechaLanzamiento().format(dtf)
//                ));

        //bsuqueda
//        System.out.println("Ingrese el titulo del episodio que desea ver: ");
//        var titulo = input.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(titulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episodio encontrado!");
//            System.out.println(episodioBuscado.get()); //trae todos los datos
//        } else {
//            System.out.println("Ups, episodio no encontrado!");
//        }

//        Map<Integer, Double> ratePorTemporada = episodios.stream()
//                .filter(e -> e.getRate() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getRate)));
//        System.out.println(ratePorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getRate() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getRate));
//        System.out.println("Media de las evaluaciones: " + est.getAverage());
//        System.out.println("Episodio mejor evaluado: " +est.getMax());
//        System.out.println("Episodio peor evaluado: " +est.getMin());
//    }
}
