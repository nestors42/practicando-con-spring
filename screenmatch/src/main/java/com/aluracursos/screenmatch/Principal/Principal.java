package com.aluracursos.screenmatch.Principal;

import com.aluracursos.screenmatch.model.DatosEpisodios;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=bb208c9";
    private ConvierteDatos conversor = new ConvierteDatos();


    public void muestraElMenu(){
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar");
//        Busca los datos generales de las series
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);
        List<DatosTemporadas> temporadas = new ArrayList<>();
//        Busca los datos de todas las temporadas
        for (int i = 1; i <=datos.totalDeTemporadas() ; i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season="+i+ API_KEY);
            var datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporada);

        }
//        temporadas.forEach(System.out::println);

//        Mostrar solo el titulo de los episodios para las temporadas
//        temporadas.forEach(t ->t.episodios().forEach(e -> System.out.println(e.titulo())));

//        aca vamos a convertir las informaciones a una lista del tipo datos episodio
        List<DatosEpisodios> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toUnmodifiableList());

//        vamos a crear un to 5 de peliculas
//        datosEpisodios.stream()
//                .filter(e-> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DatosEpisodios::evaluacion).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t-> t.episodios().stream()
                        .map(d-> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

//        episodios.forEach(System.out::println);

//        Busqueda de episodios x año
//        System.out.println("Escriba el año que desea buscar");
//        var fecha = teclado.nextInt();
//        teclado.nextLine();
//        LocalDate fechaDeBusqueda = LocalDate.of(fecha, 1, 1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//        episodios.stream()
//                .filter(e-> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaDeBusqueda))
//                .forEach(e-> System.out.println("Temporada:" + e.getTemprorada() + "Episodio:" + e.getTitulo() + "Fecha de lanzamiento:" + e.getFechaDeLanzamiento()));

//        busca episodio por pedazo del titulo

//        System.out.println("Escriba el titulo que desea ver");
//        var pedazoTitulo = teclado.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println(" Episodio encontrado");
//            System.out.println(" Los datos son: " + episodioBuscado.get());
//        }else {
//            System.out.println("Episodio no encontrado ");
//        }

        Map<Integer , Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemprorada, Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("La media de las evaluaciones: " + est.getAverage());
        System.out.println("Episodio mejor evaluado: " + est.getMax());
    }
}
