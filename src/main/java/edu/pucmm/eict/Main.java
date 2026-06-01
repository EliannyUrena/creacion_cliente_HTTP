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

            IO.println("1. Cantidad de lineas: " + cantidadLineas(stringHtml));
            IO.println("2. Cantidad de parrafos: " + cantidadParrafos(document));
            IO.println("3. Cantidad de imagenes dentro de los parrafos: " + cantidadImagenesParrafos(document));
            IO.println("4. Cantidad de formularios: ");
            IO.println("GET: "+ cantidadFormulariosGET(document));
            IO.println("POST "+ cantidadFormulariosPOST(document));
            IO.println("\n5. Inputs para cada formulario: ");
            mostrarInputs(document);

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

    public static int cantidadParrafos(Document doc)
    {
        return doc.select("p").size();
    }

    public static int cantidadImagenesParrafos(Document doc)
    {
        return doc.select("p img").size();
    }

    public static int cantidadFormulariosGET(Document doc)
    {
        Elements formularios = doc.select("form");

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

    public static int cantidadFormulariosPOST(Document doc)
    {
        Elements formularios = doc.select("form");

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

    public static void mostrarInputs(Document doc)
    {
        Elements formularios = doc.select("form");

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
                IO.println("Input: "+nombre);
                IO.println("Tipo: "+tipo);
            }
            cant++;
        }

    }
}
