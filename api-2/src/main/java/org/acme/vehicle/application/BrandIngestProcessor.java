package org.acme.vehicle.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import java.time.Instant;
import org.acme.vehicle.infrastructure.fipe.FipeModeloJson;
import org.acme.vehicle.infrastructure.fipe.FipeModelosClient;
import org.acme.vehicle.infrastructure.fipe.FipeModelosResponseJson;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;
import org.acme.vehicle.messaging.BrandIngestMessage;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BrandIngestProcessor {

    private static final Logger LOG = Logger.getLogger(BrandIngestProcessor.class);

    private final FipeModelosClient fipeModelosClient;

    @Inject
    public BrandIngestProcessor(@RestClient FipeModelosClient fipeModelosClient) {
        this.fipeModelosClient = fipeModelosClient;
    }

    @Transactional
    public void process(BrandIngestMessage msg) {
        BrandEntity brand =
                BrandEntity.findByFipeCode(msg.fipeCode())
                        .orElseGet(
                                () -> {
                                    BrandEntity b = new BrandEntity();
                                    b.fipeCode = msg.fipeCode();
                                    b.name = msg.name();
                                    b.createdAt = Instant.now();
                                    b.persist();
                                    LOG.infof("Registro inserido no banco (brand): %s", b);
                                    return b;
                                });
        if (msg.name() != null && !msg.name().equals(brand.name)) {
            brand.name = msg.name();
        }

        FipeModelosResponseJson response;
        try {
            response = fipeModelosClient.getModelos(msg.fipeCode());
        } catch (WebApplicationException | ProcessingException e) {
            LOG.errorf(e, "Falha FIPE ao buscar modelos da marca %s", msg.fipeCode());
            throw e;
        }

        if (response == null || response.getModelos() == null) {
            return;
        }

        for (FipeModeloJson modelo : response.getModelos()) {
            if (modelo.getCodigo() == null) {
                continue;
            }
            int code = modelo.getCodigo();
            if (VehicleEntity.existsByBrandAndFipeModel(brand.id, code)) {
                continue;
            }
            VehicleEntity v = new VehicleEntity();
            v.brand = brand;
            v.fipeModelCode = code;
            v.modelName = modelo.getNome() != null ? modelo.getNome() : "";
            v.createdAt = Instant.now();
            v.persist();
            LOG.infof("Registro inserido no banco (vehicle): %s", v);
        }
    }
}
