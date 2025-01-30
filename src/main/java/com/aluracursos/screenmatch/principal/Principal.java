package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=597dbcb5";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;



    public Principal(SerieRepository repository) {
        this.repositorio = repository;

    }

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4- buscar series por titulo
                    5- Top 5 mejores series
                    6- Buscar series por categoria
                    7 - Filtrar series
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

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
                    buscarSerieTitulo();
                    break;

                case 5:
                    buscarTop5Series();
                    break;

                case 6:
                    buscarSeriePorCategoria();
                    break;

                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }


    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();


        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            //Obtenemos el valor de la <optional> Serie
            var serieEncontrada = serie.get();
            //DatosSerie datosSerie = getDatosSerie();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios  = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map( e -> new Episodio(d.numero(),e)))
                            .collect(Collectors.toList());

            //serieEncontrada tiene un valor de serie.get() -> este tiene guardado una serie de tipo Serie que tiene metodos
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);


        }


    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie =new Serie(datos);
        repositorio.save(serie);


        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {

        // va a encontrar todas las series que tenemos guardado en la BBDD
        series=repositorio.findAll();


        /*

        List<Serie> series = new ArrayList<>();
        series = datosSeries.stream()
                .map(d -> new Serie(d))
                .collect(Collectors.toList());

         */

        // ordenar a traves de genero
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSerieTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);


        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        }else{
            System.out.println("Serie no encontrada");
        }

    }

    private void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();

        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + "Evaluación: " + s.getEvaluacion()) );

    }


    private void buscarSeriePorCategoria(){
        System.out.println("Escriba el genero/categoría de la serie que desea buscar");
        var genero=  teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " +  genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    public void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        //List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(totalTemporadas, evaluacion);
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);

        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }




}

