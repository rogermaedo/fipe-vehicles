package org.acme.vehicle.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.Response;
import org.acme.vehicle.application.exception.BrandIngestPublishException;
import org.acme.vehicle.contract.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class BrandIngestPublishExceptionMapperTest {

    @Test
    void mapsTo503WithErrorBody() {
        var mapper = new BrandIngestPublishExceptionMapper();
        var ex = new BrandIngestPublishException("fila indisponível", new RuntimeException());

        Response r = mapper.toResponse(ex);

        assertEquals(503, r.getStatus());
        assertTrue(r.getEntity() instanceof ErrorResponse);
        assertEquals("fila indisponível", ((ErrorResponse) r.getEntity()).getMessage());
    }
}
