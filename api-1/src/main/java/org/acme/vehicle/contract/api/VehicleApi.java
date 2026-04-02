package org.acme.vehicle.contract.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.vehicle.contract.model.VehiclePatchRequest;

/**
 * Contrato REST gerado conceitualmente a partir de {@code META-INF/openapi.yaml} (API-first / jaxrs-spec,
 * {@code returnResponse=true}). Regenerar com {@code openapi-generator-maven-plugin} quando o YAML mudar.
 */
@Path("/api/v1")
public interface VehicleApi {

    @POST
    @Path("/initial-load")
    @Produces(MediaType.APPLICATION_JSON)
    Response triggerInitialLoad();

    @GET
    @Path("/brands")
    @Produces(MediaType.APPLICATION_JSON)
    Response listBrands();

    @GET
    @Path("/brands/{brandId}/vehicles")
    @Produces(MediaType.APPLICATION_JSON)
    Response listVehiclesByBrand(@PathParam("brandId") Long brandId);

    @GET
    @Path("/vehicles/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getVehicleById(@PathParam("id") Long id);

    @PATCH
    @Path("/vehicles/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response patchVehicle(@PathParam("id") Long id, VehiclePatchRequest vehiclePatchRequest);
}
