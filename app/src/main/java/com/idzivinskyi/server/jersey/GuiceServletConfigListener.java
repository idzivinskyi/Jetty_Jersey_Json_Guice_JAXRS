package com.idzivinskyi.server.jersey;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;

@Singleton
public class GuiceServletConfigListener extends GuiceServletContextListener {

    @Inject
    private Injector injector;

    @Override
    protected Injector getInjector() {
        return injector;
    }
}
