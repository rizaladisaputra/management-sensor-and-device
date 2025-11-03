package com.controller;

import com.model.Device;
import com.service.DeliveryService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/data")
public class DataDeliveryController {


    @Inject
    DeliveryService deliveryService;


    @POST
    @Path("/send/{deviceId}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response send(@PathParam("deviceId") Long deviceId, String payload) {
        Device device = Device.findById(deviceId);
        if (device == null) return Response.status(Response.Status.NOT_FOUND).build();
        deliveryService.enqueueDelivery(device, payload);
        return Response.accepted().build();
    }
}
