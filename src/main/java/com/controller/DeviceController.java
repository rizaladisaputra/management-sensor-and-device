package com.controller;

import com.model.Device;
import com.model.Sensor;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/devices")

public class DeviceController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(Device.listAll()).build();
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        Device device = Device.findById(id);
        if (device == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        return Response.ok(device).build();
    }


    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Device device) {
        device.persist();
        return Response.created(URI.create("/devices/" + device.id)).entity(device).build();
    }


    @PUT
    @Path("{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Device input) {
        Device device = Device.findById(id);
        if (device == null) return Response.status(Response.Status.NOT_FOUND).build();
        device.name = input.name;
        device.clientCallbackUrl = input.clientCallbackUrl;
        device.active = input.active;
        return Response.ok(device).build();
    }


    @DELETE
    @Path("{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        Device d = Device.findById(id);
        if (d == null) return Response.status(Response.Status.NOT_FOUND).build();
        d.delete();
        return Response.noContent().build();
    }
}
