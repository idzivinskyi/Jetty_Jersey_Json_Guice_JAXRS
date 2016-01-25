package com.idzivinskyi.server;

import com.idzivinskyi.api.HelloApi;

public class HelloRestService implements HelloApi {

    @Override
    public String hello() {
        return "Hello World";
    }
}
