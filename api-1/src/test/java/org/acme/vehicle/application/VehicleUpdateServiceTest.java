package org.acme.vehicle.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.Optional;
import org.acme.vehicle.contract.model.VehiclePatchRequest;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleUpdateServiceTest {

    @Test
    void patch_vehicleMissing_returnsEmpty() {
        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(1L)).thenReturn(Optional.empty());

            VehicleUpdateService svc = new VehicleUpdateService();
            var req = new VehiclePatchRequest();
            req.setModelName("x");

            assertTrue(svc.patch(1L, req).isEmpty());
        }
    }

    @Test
    void patch_updatesModelNameAndNotes() {
        BrandEntity brand = new BrandEntity();
        brand.id = 2L;
        VehicleEntity e = new VehicleEntity();
        e.id = 9L;
        e.brand = brand;
        e.fipeModelCode = 1;
        e.modelName = "Old";
        e.notes = "old-notes";
        e.createdAt = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(9L)).thenReturn(Optional.of(e));

            VehicleUpdateService svc = new VehicleUpdateService();
            var req = new VehiclePatchRequest();
            req.setModelName("New");
            req.setNotes("updated");

            var out = svc.patch(9L, req);

            assertTrue(out.isPresent());
            assertEquals("New", e.modelName);
            assertEquals("updated", e.notes);
            assertEquals("New", out.get().getModelName());
            assertEquals("updated", out.get().getNotes());
        }
    }

    @Test
    void patch_notesEmptyString_becomesNull() {
        BrandEntity brand = new BrandEntity();
        brand.id = 1L;
        VehicleEntity e = new VehicleEntity();
        e.id = 1L;
        e.brand = brand;
        e.fipeModelCode = 1;
        e.modelName = "M";
        e.notes = "x";
        e.createdAt = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(1L)).thenReturn(Optional.of(e));

            VehicleUpdateService svc = new VehicleUpdateService();
            var req = new VehiclePatchRequest();
            req.setNotes("");

            var out = svc.patch(1L, req);

            assertTrue(out.isPresent());
            assertNull(e.notes);
            assertNull(out.get().getNotes());
        }
    }

    @Test
    void patch_onlyModelName_leavesNotesUnchanged() {
        BrandEntity brand = new BrandEntity();
        brand.id = 1L;
        VehicleEntity e = new VehicleEntity();
        e.id = 1L;
        e.brand = brand;
        e.fipeModelCode = 1;
        e.modelName = "A";
        e.notes = "keep";
        e.createdAt = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(1L)).thenReturn(Optional.of(e));

            VehicleUpdateService svc = new VehicleUpdateService();
            var req = new VehiclePatchRequest();
            req.setModelName("B");

            svc.patch(1L, req);

            assertEquals("B", e.modelName);
            assertEquals("keep", e.notes);
        }
    }
}
