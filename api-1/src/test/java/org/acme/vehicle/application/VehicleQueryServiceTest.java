package org.acme.vehicle.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.acme.vehicle.contract.model.VehicleResponse;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleQueryServiceTest {

    @Test
    void listByBrandId_brandMissing_returnsEmpty() {
        // findByIdOptional está declarado em PanacheEntityBase; o invokestatic no bytecode aponta para lá.
        try (MockedStatic<PanacheEntityBase> mp = mockStatic(PanacheEntityBase.class)) {
            mp.when(() -> PanacheEntityBase.findByIdOptional(7L)).thenReturn(Optional.empty());

            VehicleQueryService svc = new VehicleQueryService();
            assertTrue(svc.listByBrandId(7L).isEmpty());
        }
    }

    @Test
    void listByBrandId_brandFound_mapsVehicles() {
        BrandEntity brand = new BrandEntity();
        brand.id = 1L;

        VehicleEntity v = vehicleRow(brand, 10L, 5940, "Corolla");

        try (MockedStatic<PanacheEntityBase> mp = mockStatic(PanacheEntityBase.class);
                MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mp.when(() -> PanacheEntityBase.findByIdOptional(1L)).thenReturn(Optional.of(brand));
            mv.when(() -> VehicleEntity.listByBrandId(1L)).thenReturn(List.of(v));

            VehicleQueryService svc = new VehicleQueryService();
            Optional<List<VehicleResponse>> opt = svc.listByBrandId(1L);

            assertTrue(opt.isPresent());
            assertEquals(1, opt.get().size());
            assertEquals(10L, opt.get().get(0).getId());
            assertEquals(1L, opt.get().get(0).getBrandId());
            assertEquals(5940, opt.get().get(0).getFipeModelCode());
            assertEquals("Corolla", opt.get().get(0).getModelName());
        }
    }

    @Test
    void findById_missing_returnsEmpty() {
        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(99L)).thenReturn(Optional.empty());

            VehicleQueryService svc = new VehicleQueryService();
            assertFalse(svc.findById(99L).isPresent());
        }
    }

    @Test
    void findById_found_returnsResponse() {
        BrandEntity brand = new BrandEntity();
        brand.id = 3L;
        VehicleEntity v = vehicleRow(brand, 5L, 100, "X");

        try (MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class)) {
            mv.when(() -> VehicleEntity.findVehicleById(5L)).thenReturn(Optional.of(v));

            VehicleQueryService svc = new VehicleQueryService();
            Optional<VehicleResponse> r = svc.findById(5L);

            assertTrue(r.isPresent());
            assertEquals(5L, r.get().getId());
            assertEquals(3L, r.get().getBrandId());
        }
    }

    private static VehicleEntity vehicleRow(BrandEntity brand, Long id, int fipeCode, String modelName) {
        VehicleEntity v = new VehicleEntity();
        v.id = id;
        v.brand = brand;
        v.fipeModelCode = fipeCode;
        v.modelName = modelName;
        v.notes = "n";
        v.createdAt = Instant.parse("2024-06-01T10:00:00Z");
        return v;
    }
}
