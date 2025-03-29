package se.trilli.jettyangular.jetty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.cdi.CdiDecoratingListener;
import org.eclipse.jetty.ee10.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jboss.weld.environment.servlet.EnhancedListener;
import se.trilli.jettyangular.Application;

import java.net.URI;

import static java.awt.Desktop.Action.BROWSE;
import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;
import static java.lang.Boolean.TRUE;
import static org.eclipse.jetty.ee10.servlet.ServletContextHandler.NO_SESSIONS;

public class Jetty {
    private static final Logger LOGGER = LogManager.getLogger(Jetty.class);
    private static final int PORT = 8080;
    private static final String API_PATH = "/api/*";
    private static final String BASE_URL = "http://localhost:" + PORT + "/api/helloworld";
    private final Server server;

    public Jetty() {
        server = new Server(PORT);
        configure();
    }

    private void configure() {
        server.insertHandler(createJerseyHandler());
        server.insertHandler(createAngularHandler());
    }

    public void start() {
        try {
            server.start();
            openBrowser();
            server.join();
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred during system start.", e);
        } finally {
            server.destroy();
        }
    }

    private void openBrowser() {
        try {
            if (isDesktopSupported() && getDesktop().isSupported(BROWSE)) {
                getDesktop().browse(new URI(BASE_URL));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to open browser.", e);
        }
    }

    private Handler.Wrapper createAngularHandler() {
        ResourceHandler handler = new ResourceHandler();
        handler.setBaseResourceAsString(this.getClass().getClassLoader().getResource("webapp").toExternalForm());

        return handler;
    }

    private Handler.Wrapper createJerseyHandler() {
        ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
        context.setContextPath("/");

        context.setInitParameter(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE, CdiDecoratingListener.MODE);
        context.addServletContainerInitializer(new CdiServletContainerInitializer());
        context.addServletContainerInitializer(new EnhancedListener());

        //Jersey Servlet
        ServletHolder holder = context.addServlet(ServletContainer.class, API_PATH);
        holder.setInitOrder(0);
        holder.setAsyncSupported(true);
        holder.setInitParameter("jersey.config.server.provider.packages", Application.class.getPackageName());
        holder.setInitParameter("jersey.config.server.provider.scanning.recursive", TRUE.toString());

        return context;
    }
}