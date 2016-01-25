package com.idzivinskyi;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.idzivinskyi.module.AppModule;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        final Injector injector = Guice.createInjector(new AppModule());

        final Server server = injector.getInstance(Server.class);

        try {
            server.setStopAtShutdown(true);
            server.start();
            server.join();
        } catch (Exception e) {
            log.error("Server can't start", e);
        }
    }
}
