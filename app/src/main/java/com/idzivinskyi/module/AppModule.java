package com.idzivinskyi.module;

import com.google.gson.Gson;
import com.google.inject.servlet.ServletModule;
import com.idzivinskyi.util.GsonBuilderHelper;

public class AppModule extends ConfigurableModule {

    public AppModule() {
        super("application.properties");
    }

    @Override
    protected void configure() {
        super.configure();

        bind(Gson.class).toInstance(GsonBuilderHelper.builder().disableHtmlEscaping().create());

        install(new ServerModule());
        install(new ServletModule());
        install(new BuildModule());
    }
}
