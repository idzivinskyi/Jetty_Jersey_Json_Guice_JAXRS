package com.idzivinskyi;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import com.idzivinskyi.module.BuildModule;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        final Injector injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(BuildModule.class);
            }
        }, new ServletModule());

        Server server = injector.getInstance(HttpServer.class).getServer(injector);

        server.start();

        server.join();
    }
}
