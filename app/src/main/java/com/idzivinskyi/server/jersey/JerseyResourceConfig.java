package com.idzivinskyi.server.jersey;

import com.google.gson.Gson;
import com.google.inject.Injector;

import com.idzivinskyi.server.HelloRestService;
import com.idzivinskyi.util.GsonProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

public class JerseyResourceConfig extends ResourceConfig {
    private static final Logger log = LoggerFactory.getLogger(JerseyResourceConfig.class);

    private static final AtomicReference<Injector> injector = new AtomicReference<>();

    public static void setInjector(Injector i) {
        injector.compareAndSet(null, i);
    }

    @Inject
    public JerseyResourceConfig(ServiceLocator serviceLocator) {
        register(new GsonProvider(injector.get().getInstance(Gson.class)));

        //Debug
        if (log.isDebugEnabled()) {
            register(LoggingFilter.class);
            property(ServerProperties.TRACING, TracingConfig.ON_DEMAND.name());
        }

        register(HelloRestService.class);

        log.info("Registering injectables...");
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector.get());
    }
}
