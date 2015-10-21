package org.tiogasolutions.lib.jaxrs.jackson.server;

import java.io.IOException;
import java.net.URI;
import org.tiogasolutions.lib.jaxrs.JavaTimeParamConverterProvider;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class FreeBirdRestServer {

  // Base URI the Grizzly HTTP server will listen on
  public static final int testPort = 8099;
  public static final URI BASE_URI = URI.create("http://localhost:"+testPort+"/test-server/");
  public static final URI API_URI  = URI.create("http://localhost:"+testPort+"/test-server/api");

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in com.example package

    String[] packages = new String[] {
        JavaTimeParamConverterProvider.class.getPackage().getName(),
        JacksonReaderWriterProvider.class.getPackage().getName(),
        FreeBirdRestController.class.getPackage().getName()
    };

    final ResourceConfig rc = new ResourceConfig().packages(packages);

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
  }

  /**
   * Main method.
   * @param args
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));

    java.awt.Desktop.getDesktop().browse(URI.create(BASE_URI+"api"));

    // noinspection ResultOfMethodCallIgnored
    System.in.read();
    server.shutdownNow();
  }
}
