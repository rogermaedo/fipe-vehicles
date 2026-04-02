package org.acme.vehicle.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.acme.vehicle.contract.model.VehicleResponse;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;

@ApplicationScoped
public class VehicleQueryService {

    @Transactional
    public Optional<List<VehicleResponse>> listByBrandId(Long brandId) {
        if (BrandEntity.findByIdOptional(brandId).isEmpty()) {
            return Optional.empty();
        }
        List<VehicleResponse> list = VehicleEntity.listByBrandId(brandId).stream()
                .map(this::toResponse)
                .toList();
        return Optional.of(list);
    }

    @Transactional
    public Optional<VehicleResponse> findById(Long id) {
        return VehicleEntity.findVehicleById(id).map(this::toResponse);
    }

    private VehicleResponse toResponse(VehicleEntity e) {
        return new VehicleResponse(
                e.id,
                e.brand.id,
                e.fipeModelCode,
                e.modelName,
                e.notes,
                e.createdAt.atOffset(ZoneOffset.UTC));
    }
}
