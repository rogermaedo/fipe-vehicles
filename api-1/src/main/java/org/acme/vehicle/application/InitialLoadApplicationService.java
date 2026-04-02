package org.acme.vehicle.application;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.acme.vehicle.application.port.BrandIngestPublisher;
import org.acme.vehicle.application.port.FipeBrandsCatalog;
import org.acme.vehicle.domain.model.FipeBrand;

@ApplicationScoped
public class InitialLoadApplicationService {

    private final FipeBrandsCatalog fipeBrandsCatalog;
    private final BrandIngestPublisher brandIngestPublisher;

    public InitialLoadApplicationService(FipeBrandsCatalog fipeBrandsCatalog, BrandIngestPublisher brandIngestPublisher) {
        this.fipeBrandsCatalog = fipeBrandsCatalog;
        this.brandIngestPublisher = brandIngestPublisher;
    }

    /**
     * Busca marcas na FIPE e publica uma mensagem por marca na fila. Retorna quantidade enfileirada.
     */
    public int executeInitialLoad() {
        List<FipeBrand> brands = fipeBrandsCatalog.fetchAll();
        brandIngestPublisher.publishAll(brands);
        return brands.size();
    }
}
