package org.acme.vehicle.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.vehicle.application.BrandQueryService;
import org.acme.vehicle.application.InitialLoadApplicationService;
import org.acme.vehicle.application.VehicleQueryService;
import org.acme.vehicle.application.VehicleUpdateService;
import org.acme.vehicle.contract.api.VehicleApi;
import org.acme.vehicle.contract.model.ErrorResponse;
import org.acme.vehicle.contract.model.InitialLoadAcceptedResponse;
import org.acme.vehicle.contract.model.VehiclePatchRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Tag(name = "vehicle-api", description = "Implementação do contrato OpenAPI (API-first).")
public class VehicleApiResource implements VehicleApi {

    private final InitialLoadApplicationService initialLoadApplicationService;
    private final BrandQueryService brandQueryService;
    private final VehicleQueryService vehicleQueryService;
    private final VehicleUpdateService vehicleUpdateService;

    @Inject
    public VehicleApiResource(
            InitialLoadApplicationService initialLoadApplicationService,
            BrandQueryService brandQueryService,
            VehicleQueryService vehicleQueryService,
            VehicleUpdateService vehicleUpdateService) {
        this.initialLoadApplicationService = initialLoadApplicationService;
        this.brandQueryService = brandQueryService;
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleUpdateService = vehicleUpdateService;
    }

    @Override
    @Operation(operationId = "triggerInitialLoad")
    public Response triggerInitialLoad() {
        int enqueued = initialLoadApplicationService.executeInitialLoad();
        return Response.accepted(new InitialLoadAcceptedResponse(enqueued)).build();
    }

    @Override
    @Operation(operationId = "listBrands")
    public Response listBrands() {
        return Response.ok(brandQueryService.listAll()).build();
    }

    @Override
    @Operation(operationId = "listVehiclesByBrand")
    public Response listVehiclesByBrand(Long brandId) {
        return vehicleQueryService
                .listByBrandId(brandId)
                .map(list -> Response.ok(list).build())
                .orElseThrow(
                        () ->
                                new WebApplicationException(
                                        Response.status(Response.Status.NOT_FOUND)
                                                .entity(new ErrorResponse("Marca não encontrada."))
                                                .build()));
    }

    @Override
    @Operation(operationId = "getVehicleById")
    public Response getVehicleById(Long id) {
        return vehicleQueryService
                .findById(id)
                .map(v -> Response.ok(v).build())
                .orElseThrow(
                        () ->
                                new WebApplicationException(
                                        Response.status(Response.Status.NOT_FOUND)
                                                .entity(new ErrorResponse("Veículo não encontrado."))
                                                .build()));
    }

    @Override
    @Operation(operationId = "patchVehicle")
    public Response patchVehicle(Long id, VehiclePatchRequest vehiclePatchRequest) {
        if (vehiclePatchRequest == null
                || (vehiclePatchRequest.getModelName() == null && vehiclePatchRequest.getNotes() == null)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Informe modelName e/ou notes."))
                            .build());
        }
        return vehicleUpdateService
                .patch(id, vehiclePatchRequest)
                .map(v -> Response.ok(v).build())
                .orElseThrow(
                        () ->
                                new WebApplicationException(
                                        Response.status(Response.Status.NOT_FOUND)
                                                .entity(new ErrorResponse("Veículo não encontrado."))
                                                .build()));
    }
}
