package org.acme.vehicle.infrastructure.fipe;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "fipe-api")
public interface FipeModelosClient {

    @GET
    @Path("/carros/marcas/{codigoMarca}/modelos")
    @Produces(MediaType.APPLICATION_JSON)
    FipeModelosResponseJson getModelos(@PathParam("codigoMarca") String codigoMarca);
}
