package com.idzivinskyi;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class JerseyResourceConfig extends ResourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyResourceConfig.class);

    private static Injector injector;

    public static void setInjector(Injector injector) {
        JerseyResourceConfig.injector = injector;
    }

    @Inject
    public JerseyResourceConfig(ServiceLocator serviceLocator) {
        // Set package to look for resources in
        packages("com.idzivinskyi.api");

        LOGGER.info("Registering injectables...");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }
}
