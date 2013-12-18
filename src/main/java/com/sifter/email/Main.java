package com.sifter.email;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sifter.email.lib.CategoryEnum;
import com.sifter.email.lib.GateResources;
import com.sifter.email.lib.StanfordResources;

import gate.util.GateException;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.sifter.email package
        final ResourceConfig rc = new ResourceConfig().packages("com.sifter.email");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     * @throws GateException 
     */
    public static void main(String[] args) throws IOException, GateException {
        final HttpServer server = startServer();
        System.out.println(CategoryEnum.ThreadHeader.getCategory());
        GateResources gr = GateResources.getInstance();
        StanfordResources sr = StanfordResources.getInstance();
        gr.initialize();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

