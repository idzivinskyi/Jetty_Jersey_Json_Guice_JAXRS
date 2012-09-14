package com.jax.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.jax.service.HelloGuice;
import com.jax.service.HelloWorldService;
import com.jax.service.JSONService;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                // Must configure at least one JAX-RS resource or the
                // server will fail to start.
                bind(HelloGuice.class);
                bind(HelloWorldService.class);
                bind(JSONService.class);

                bind(GuicyInterface.class).to(GuicyInterfaceImpl.class);

                // Route all requests through GuiceContainer
                serve("/*").with(GuiceContainer.class);
            }
        });
    }
}