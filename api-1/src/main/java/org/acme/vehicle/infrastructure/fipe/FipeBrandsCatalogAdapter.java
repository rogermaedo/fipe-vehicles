package org.acme.vehicle.infrastructure.fipe;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.acme.vehicle.application.exception.FipeCatalogUnavailableException;
import org.acme.vehicle.application.port.FipeBrandsCatalog;
import org.acme.vehicle.domain.model.FipeBrand;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class FipeBrandsCatalogAdapter implements FipeBrandsCatalog {

    private final FipeApiClient client;

    public FipeBrandsCatalogAdapter(@RestClient FipeApiClient client) {
        this.client = client;
    }

    @Override
    public List<FipeBrand> fetchAll() {
        try {
            return client.listMarcas().stream().map(m -> new FipeBrand(m.codigo(), m.nome())).toList();
        } catch (WebApplicationException e) {
            throw new FipeCatalogUnavailableException("A API FIPE retornou erro HTTP.", e);
        } catch (ProcessingException e) {
            throw new FipeCatalogUnavailableException("Falha ao contactar a API FIPE (rede ou timeout).", e);
        }
    }
}
