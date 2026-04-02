package org.acme.vehicle.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.List;
import org.acme.vehicle.contract.model.BrandResponse;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandQueryServiceTest {

    @Test
    void listAll_mapsEntitiesToContract() {
        BrandEntity b1 = new BrandEntity();
        b1.id = 1L;
        b1.fipeCode = "59";
        b1.name = "Toyota";
        b1.createdAt = Instant.parse("2024-01-01T00:00:00Z");

        try (MockedStatic<BrandEntity> mocked = mockStatic(BrandEntity.class)) {
            mocked.when(BrandEntity::listAllOrderedByName).thenReturn(List.of(b1));

            BrandQueryService svc = new BrandQueryService();
            List<BrandResponse> out = svc.listAll();

            assertEquals(1, out.size());
            assertEquals(1L, out.get(0).getId());
            assertEquals("59", out.get(0).getFipeCode());
            assertEquals("Toyota", out.get(0).getName());
        }
    }

    @Test
    void listAll_empty_returnsEmptyList() {
        try (MockedStatic<BrandEntity> mocked = mockStatic(BrandEntity.class)) {
            mocked.when(BrandEntity::listAllOrderedByName).thenReturn(List.of());

            BrandQueryService svc = new BrandQueryService();
            assertTrue(svc.listAll().isEmpty());
        }
    }
}
