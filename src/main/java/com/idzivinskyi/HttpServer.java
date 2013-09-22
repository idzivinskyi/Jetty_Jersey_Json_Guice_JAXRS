package com.idzivinskyi;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class HttpServer {

    public Server getServer(Injector injector) {
        final Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ServletHolder jerseyServletHolder = new ServletHolder(new ServletContainer());
        JerseyResourceConfig.setInjector(injector);
        jerseyServletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyResourceConfig.class.getName());

        context.addServlet(jerseyServletHolder, "/*");

        context.addFilter(GuiceFilter.class, "/*", null);
        context.addEventListener(new GuiceServletConfigListener(injector));
        server.setHandler(context);

        return server;
    }
}
