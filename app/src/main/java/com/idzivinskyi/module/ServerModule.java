package com.idzivinskyi.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.idzivinskyi.server.jetty.HttpServer;
import org.eclipse.jetty.server.Server;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Server.class).toProvider(HttpServer.class).in(Singleton.class);
    }
}
