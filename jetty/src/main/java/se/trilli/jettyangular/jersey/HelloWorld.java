package se.trilli.jettyangular.jersey;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import se.trilli.jettyangular.inject.HelloService;


@Path("/helloworld")
public class HelloWorld {
    private HelloService helloService;

    @Inject
    public HelloWorld(HelloService helloService) {
        this.helloService = helloService;
    }

    @GET
    public Response get() {
        return Response.ok().entity(helloService.getMessage()).build();
    }
}