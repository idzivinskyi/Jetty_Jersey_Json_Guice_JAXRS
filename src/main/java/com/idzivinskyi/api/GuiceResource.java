package com.idzivinskyi.api;


import com.google.inject.servlet.RequestScoped;
import com.idzivinskyi.module.BuildModule;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
@RequestScoped
public class GuiceResource {

    @Inject
    private BuildModule buildModule;

    public GuiceResource() {
        System.out.println(1);
    }

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return buildModule != null ? buildModule.testData() : "inject is fail";
    }
}
