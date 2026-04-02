package org.acme.vehicle.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import org.acme.vehicle.contract.model.BrandResponse;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;

@ApplicationScoped
public class BrandQueryService {

    @Transactional
    public List<BrandResponse> listAll() {
        return BrandEntity.listAllOrderedByName().stream()
                .map(b -> new BrandResponse(b.id, b.fipeCode, b.name))
                .toList();
    }
}
