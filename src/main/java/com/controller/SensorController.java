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

@Path("/sensors")
public class SensorController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(Sensor.listAll()).build();
    }


    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Sensor sensor) {
        if (sensor.device != null && sensor.device.id != null) {
            sensor.device = Device.findById(sensor.device.id);
        }
        sensor.persist();
        return Response.created(URI.create("/sensors/" + sensor.id)).entity(sensor).build();
    }


    @PUT
    @Path("{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Sensor input) {
        Sensor sensor = Sensor.findById(id);
        if (sensor == null) return Response.status(Response.Status.NOT_FOUND).build();
        sensor.name = input.name;
        sensor.unit = input.unit;
        if (input.device != null && input.device.id != null) sensor.device = Device.findById(input.device.id);
        sensor.active = input.active;
        return Response.ok(sensor).build();
    }


    @DELETE
    @Path("{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        Sensor sensor = Sensor.findById(id);
        if (sensor == null) return Response.status(Response.Status.NOT_FOUND).build();
        sensor.delete();
        return Response.noContent().build();
    }
}
