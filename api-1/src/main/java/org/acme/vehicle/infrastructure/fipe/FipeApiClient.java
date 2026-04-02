package org.acme.vehicle.infrastructure.fipe;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/carros/marcas")
@RegisterRestClient(configKey = "fipe-api")
public interface FipeApiClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<FipeMarcaJson> listMarcas();
}
