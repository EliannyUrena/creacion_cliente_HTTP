package edu.pucmm.eict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String url;

        do{
            System.out.println("Ingrese una URL: ");
            url = scanner.nextLine().trim();

        }while(!esURLvalida(url));

        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String tipoArchivo = response.headers().firstValue("Content-Type").orElse("Desconocido");

            IO.println("a) Tipo de recurso: " + tipoArchivo);

            if(!tipoArchivo.contains("text/html")) {
                IO.println("El recurso debe ser html");
                return;
            }

            String html = response.body();
            Document document = Jsoup.parse(response.body());
            String stringHtml = document.toString();

            Elements parrafos = document.select("p");
            Elements imgParrafos = document.select("p img");
            Elements formularios = document.select("form");

            IO.println("1. Cantidad de lineas: " + cantidadLineas(stringHtml));
            IO.println("2. Cantidad de parrafos: " + parrafos.size());
            IO.println("3. Cantidad de imagenes dentro de los parrafos: " + imgParrafos.size());
            IO.println("4. Cantidad de formularios: ");
            IO.println("GET: "+ cantidadFormulariosGET(formularios));
            IO.println("POST "+ cantidadFormulariosPOST(formularios));
            IO.println("\n5. Inputs para cada formulario: ");
            mostrarInputs(formularios);
            IO.println("\n6. Enviar formularios POST: ");
            enviarFormulariosPOST(formularios, client);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean esURLvalida(String url) {

        try {
            URI uri = new URI(url);

            if (uri.getScheme() != null && uri.getHost() != null) {
                return true;
            }

        } catch (URISyntaxException e) {
            System.out.println("La URL es inválida" + e.getMessage());
            return false;
        }
        return false;
    }

    public static int cantidadLineas(String html)
    {
        return html.split("\n").length;
    }

    public static int cantidadFormulariosGET(Elements formularios)
    {
        int get = 0;

        for (Element form : formularios)
        {
            String metodo = form.attr("method").toUpperCase();

            if(metodo.isEmpty() || metodo.equals("GET")) {
                get++;
            }
        }
        return get;
    }

    public static int cantidadFormulariosPOST(Elements formularios)
    {
        int post = 0;

        for (Element form : formularios)
        {
            String metodo = form.attr("method").toUpperCase();

            if(metodo.equals("POST")) {
                post++;
            }
        }
        return post;
    }

    public static void mostrarInputs(Elements formularios)
    {
        int cant = 1;

        for (Element form : formularios)
        {
            IO.println("\nFormulario (" +cant+ ")");

            Elements inputs = form.select("input");

            for (Element input : inputs)
            {
                String nombre = input.attr("name");
                String tipo = input.attr("type");

                if (tipo.isEmpty()) {
                    tipo = "text";
                }
                IO.println("Input: "+nombre + " - Tipo: " +tipo);
            }
            cant++;
        }
    }

    public static void enviarFormulariosPOST(Elements formularios, HttpClient client) {

        String metodo;
        String action;

        for (Element form : formularios)
        {
            try {
                metodo = form.attr("method").toUpperCase();
                action = form.attr("action");

                if (metodo.equals("POST") && !action.isEmpty())
                {
                    IO.println("ACTION: " + action);

                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(action)).header("matricula", "10156927").POST(HttpRequest.BodyPublishers.ofString("asignatura=practica1")).build();

                    HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

                    IO.println("Respuesta: " + response.body());
                }
            } catch (Exception e) {
                IO.println("Error " + e.getMessage());
            }
        }
    }
}
