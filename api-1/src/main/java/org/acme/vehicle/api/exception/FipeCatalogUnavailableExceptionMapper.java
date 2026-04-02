package org.acme.vehicle.api.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.vehicle.application.exception.FipeCatalogUnavailableException;
import org.acme.vehicle.contract.model.ErrorResponse;

@Provider
public class FipeCatalogUnavailableExceptionMapper implements ExceptionMapper<FipeCatalogUnavailableException> {

    @Override
    public Response toResponse(FipeCatalogUnavailableException exception) {
        return Response.status(502)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
