package org.acme.vehicle.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.acme.vehicle.application.BrandQueryService;
import org.acme.vehicle.application.InitialLoadApplicationService;
import org.acme.vehicle.application.VehicleQueryService;
import org.acme.vehicle.application.VehicleUpdateService;
import org.acme.vehicle.contract.model.BrandResponse;
import org.acme.vehicle.contract.model.ErrorResponse;
import org.acme.vehicle.contract.model.InitialLoadAcceptedResponse;
import org.acme.vehicle.contract.model.VehiclePatchRequest;
import org.acme.vehicle.contract.model.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleApiResourceTest {

    @Mock
    InitialLoadApplicationService initialLoadApplicationService;

    @Mock
    BrandQueryService brandQueryService;

    @Mock
    VehicleQueryService vehicleQueryService;

    @Mock
    VehicleUpdateService vehicleUpdateService;

    VehicleApiResource resource;

    @BeforeEach
    void setUp() {
        resource =
                new VehicleApiResource(
                        initialLoadApplicationService, brandQueryService, vehicleQueryService, vehicleUpdateService);
    }

    @Test
    void triggerInitialLoad_returnsAcceptedWithCount() {
        when(initialLoadApplicationService.executeInitialLoad()).thenReturn(5);

        Response r = resource.triggerInitialLoad();

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), r.getStatus());
        assertTrue(r.getEntity() instanceof InitialLoadAcceptedResponse);
        assertEquals(5, ((InitialLoadAcceptedResponse) r.getEntity()).getBrandsEnqueued());
    }

    @Test
    void listBrands_returnsOkWithBody() {
        var brands = List.of(new BrandResponse(1L, "59", "Toyota"));
        when(brandQueryService.listAll()).thenReturn(brands);

        Response r = resource.listBrands();

        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        assertEquals(brands, r.getEntity());
    }

    @Test
    void listVehiclesByBrand_notFound_throwsWebApplicationException() {
        when(vehicleQueryService.listByBrandId(99L)).thenReturn(Optional.empty());

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.listVehiclesByBrand(99L));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
        assertTrue(ex.getResponse().getEntity() instanceof ErrorResponse);
    }

    @Test
    void listVehiclesByBrand_found_returnsOk() {
        var list = List.of(sampleVehicleResponse());
        when(vehicleQueryService.listByBrandId(1L)).thenReturn(Optional.of(list));

        Response r = resource.listVehiclesByBrand(1L);

        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        assertEquals(list, r.getEntity());
    }

    @Test
    void getVehicleById_notFound_throws() {
        when(vehicleQueryService.findById(1L)).thenReturn(Optional.empty());

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.getVehicleById(1L));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void getVehicleById_found_returnsOk() {
        var v = sampleVehicleResponse();
        when(vehicleQueryService.findById(10L)).thenReturn(Optional.of(v));

        Response r = resource.getVehicleById(10L);

        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        assertEquals(v, r.getEntity());
    }

    @Test
    void patchVehicle_nullRequest_throwsBadRequest() {
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.patchVehicle(1L, null));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void patchVehicle_bothFieldsNull_throwsBadRequest() {
        var req = new VehiclePatchRequest();

        WebApplicationException ex =
                assertThrows(WebApplicationException.class, () -> resource.patchVehicle(1L, req));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void patchVehicle_notFound_throws() {
        var req = new VehiclePatchRequest();
        req.setModelName("X");
        when(vehicleUpdateService.patch(1L, req)).thenReturn(Optional.empty());

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.patchVehicle(1L, req));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void patchVehicle_success_delegatesToService() {
        var req = new VehiclePatchRequest();
        req.setNotes("n");
        var updated = sampleVehicleResponse();
        when(vehicleUpdateService.patch(2L, req)).thenReturn(Optional.of(updated));

        Response r = resource.patchVehicle(2L, req);

        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        assertEquals(updated, r.getEntity());
        verify(vehicleUpdateService).patch(2L, req);
    }

    private static VehicleResponse sampleVehicleResponse() {
        return new VehicleResponse(
                1L,
                2L,
                5940,
                "Corolla",
                null,
                OffsetDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
    }
}
