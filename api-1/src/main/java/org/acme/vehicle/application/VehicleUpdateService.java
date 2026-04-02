package org.acme.vehicle.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.ZoneOffset;
import java.util.Optional;
import org.acme.vehicle.contract.model.VehiclePatchRequest;
import org.acme.vehicle.contract.model.VehicleResponse;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;

@ApplicationScoped
public class VehicleUpdateService {

    @Transactional
    public Optional<VehicleResponse> patch(Long vehicleId, VehiclePatchRequest req) {
        Optional<VehicleEntity> opt = VehicleEntity.findVehicleById(vehicleId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        VehicleEntity e = opt.get();
        if (req.getModelName() != null) {
            e.modelName = req.getModelName();
        }
        if (req.getNotes() != null) {
            e.notes = req.getNotes().isEmpty() ? null : req.getNotes();
        }
        return Optional.of(
                new VehicleResponse(
                        e.id,
                        e.brand.id,
                        e.fipeModelCode,
                        e.modelName,
                        e.notes,
                        e.createdAt.atOffset(ZoneOffset.UTC)));
    }
}
