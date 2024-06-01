package com.aluracursos.screenmatch.Principal;

import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        temporadas.forEach(t ->t.episodios().forEach(e -> System.out.println(e.titulo())));

    }
}
