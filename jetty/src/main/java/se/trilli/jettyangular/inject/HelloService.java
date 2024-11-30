package se.trilli.jettyangular.inject;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class HelloService {
    public String getMessage() {
        return "Hello  World";
    }
}