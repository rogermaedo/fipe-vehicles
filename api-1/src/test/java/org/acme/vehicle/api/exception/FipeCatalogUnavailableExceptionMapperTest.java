package org.acme.vehicle.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.Response;
import org.acme.vehicle.application.exception.FipeCatalogUnavailableException;
import org.acme.vehicle.contract.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class FipeCatalogUnavailableExceptionMapperTest {

    @Test
    void mapsTo502WithErrorBody() {
        var mapper = new FipeCatalogUnavailableExceptionMapper();
        var ex = new FipeCatalogUnavailableException("msg", new RuntimeException());

        Response r = mapper.toResponse(ex);

        assertEquals(502, r.getStatus());
        assertTrue(r.getEntity() instanceof ErrorResponse);
        assertEquals("msg", ((ErrorResponse) r.getEntity()).getMessage());
    }
}
