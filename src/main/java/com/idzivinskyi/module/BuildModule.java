package com.idzivinskyi.module;


import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BuildModule {

    @Inject
    public String testData() {
        return "test inject";
    }

}
