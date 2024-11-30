package se.trilli.jettyangular;

import se.trilli.jettyangular.jetty.Jetty;

public class Application {
    public static void main(String[] args) {
        new Jetty().start();
    }
}
