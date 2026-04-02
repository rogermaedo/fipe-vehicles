package org.acme.vehicle.api.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.vehicle.application.exception.BrandIngestPublishException;
import org.acme.vehicle.contract.model.ErrorResponse;

@Provider
public class BrandIngestPublishExceptionMapper implements ExceptionMapper<BrandIngestPublishException> {

    @Override
    public Response toResponse(BrandIngestPublishException exception) {
        return Response.status(503)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
