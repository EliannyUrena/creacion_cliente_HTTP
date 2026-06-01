package edu.pucmm.eict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String htmlPrueba = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Prueba</title>
        </head>
        <body>
        
            <h1>hola</h1>
        
            <p>....</p>
        
        </body>
        </html>
        """;

        IO.println("Cantidad de líneas: " + cantidadLineas(htmlPrueba));

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

            IO.println("Tipo de recurso: " + tipoArchivo);

            if(!tipoArchivo.contains("text/html")) {
                IO.println("El recurso debe ser html");
                return;
            }

            String html = response.body();
            Document document = Jsoup.parse(response.body());
            String stringHtml = document.toString();

            IO.println("Cantidad de líneas: " + cantidadLineas(stringHtml));


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

}
