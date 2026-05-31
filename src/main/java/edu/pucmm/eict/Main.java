package edu.pucmm.eict;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        IO.println("Hola");

        Scanner scanner = new Scanner(System.in);
        String url;

        do{
            System.out.println("Ingrese una URL: ");
            url = scanner.nextLine().trim();

        }while(!esURLvalida(url));


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

}
