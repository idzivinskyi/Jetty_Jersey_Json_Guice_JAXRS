package com.idzivinskyi;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfigListener extends GuiceServletContextListener {
    
    private Injector injector;
    
    public GuiceServletConfigListener(Injector injector){
        this.injector = injector;
    }
    
    @Override
    protected Injector getInjector() {
        return injector;
    }

}
