package org.acme.vehicle.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.WebApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.acme.vehicle.infrastructure.fipe.FipeModeloJson;
import org.acme.vehicle.infrastructure.fipe.FipeModelosClient;
import org.acme.vehicle.infrastructure.fipe.FipeModelosResponseJson;
import org.acme.vehicle.infrastructure.persistence.BrandEntity;
import org.acme.vehicle.infrastructure.persistence.VehicleEntity;
import org.acme.vehicle.messaging.BrandIngestMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandIngestProcessorTest {

    @Mock
    FipeModelosClient fipeModelosClient;

    BrandIngestProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new BrandIngestProcessor(fipeModelosClient);
    }

    @Test
    void process_existingBrand_createsVehiclesWhenNotDuplicate() {
        BrandEntity brand = existingBrand(10L, "59", "Toyota");
        FipeModelosResponseJson resp = modelos(
                modelo(100, "A"),
                modelo(200, "B"),
                modelo(null, "skip-null-codigo"));

        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class);
                MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class);
                MockedConstruction<VehicleEntity> cons =
                        mockConstruction(
                                VehicleEntity.class,
                                (mock, ctx) -> lenient().doNothing().when(mock).persist())) {

            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenReturn(resp);
            mv.when(() -> VehicleEntity.existsByBrandAndFipeModel(10L, 100)).thenReturn(false);
            mv.when(() -> VehicleEntity.existsByBrandAndFipeModel(10L, 200)).thenReturn(false);

            processor.process(new BrandIngestMessage("59", "Toyota"));

            assertEquals(2, cons.constructed().size());
            assertEquals(100, cons.constructed().get(0).fipeModelCode);
            assertEquals("A", cons.constructed().get(0).modelName);
            assertEquals(200, cons.constructed().get(1).fipeModelCode);
        }
    }

    @Test
    void process_skipsVehicleWhenAlreadyExists() {
        BrandEntity brand = existingBrand(1L, "59", "Toyota");
        FipeModelosResponseJson resp = modelos(modelo(100, "A"));

        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class);
                MockedStatic<VehicleEntity> mv = mockStatic(VehicleEntity.class);
                MockedConstruction<VehicleEntity> cons =
                        mockConstruction(
                                VehicleEntity.class,
                                (mock, ctx) -> lenient().doNothing().when(mock).persist())) {

            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenReturn(resp);
            mv.when(() -> VehicleEntity.existsByBrandAndFipeModel(1L, 100)).thenReturn(true);

            processor.process(new BrandIngestMessage("59", "Toyota"));

            assertEquals(0, cons.constructed().size());
        }
    }

    @Test
    void process_updatesBrandNameWhenMessageDiffers() {
        BrandEntity brand = existingBrand(1L, "59", "Old");
        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class)) {
            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenReturn(emptyModelos());

            processor.process(new BrandIngestMessage("59", "New"));

            assertEquals("New", brand.name);
        }
    }

    @Test
    void process_newBrand_persistsBrand() {
        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class);
                MockedConstruction<BrandEntity> bc =
                        mockConstruction(
                                BrandEntity.class,
                                (mock, ctx) ->
                                        lenient()
                                                .doAnswer(
                                                        inv -> {
                                                            mock.id = 77L;
                                                            return null;
                                                        })
                                                .when(mock)
                                                .persist())) {

            mb.when(() -> BrandEntity.findByFipeCode("99")).thenReturn(Optional.empty());
            when(fipeModelosClient.getModelos("99")).thenReturn(emptyModelos());

            processor.process(new BrandIngestMessage("99", "Nova"));

            assertEquals(1, bc.constructed().size());
            BrandEntity created = bc.constructed().get(0);
            assertEquals("99", created.fipeCode);
            assertEquals("Nova", created.name);
        }
    }

    @Test
    void process_nullResponse_returnsWithoutVehicles() {
        BrandEntity brand = existingBrand(1L, "59", "T");

        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class);
                MockedConstruction<VehicleEntity> cons =
                        mockConstruction(VehicleEntity.class)) {

            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenReturn(null);

            processor.process(new BrandIngestMessage("59", "T"));

            assertEquals(0, cons.constructed().size());
        }
    }

    @Test
    void process_nullModelosList_returnsWithoutVehicles() {
        BrandEntity brand = existingBrand(1L, "59", "T");
        FipeModelosResponseJson resp = new FipeModelosResponseJson();
        resp.setModelos(null);

        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class);
                MockedConstruction<VehicleEntity> cons =
                        mockConstruction(VehicleEntity.class)) {

            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenReturn(resp);

            processor.process(new BrandIngestMessage("59", "T"));

            assertEquals(0, cons.constructed().size());
        }
    }

    @Test
    void process_fipeHttpError_propagates() {
        BrandEntity brand = existingBrand(1L, "59", "T");

        try (MockedStatic<BrandEntity> mb = mockStatic(BrandEntity.class)) {
            mb.when(() -> BrandEntity.findByFipeCode("59")).thenReturn(Optional.of(brand));
            when(fipeModelosClient.getModelos("59")).thenThrow(new WebApplicationException(502));

            assertThrows(
                    WebApplicationException.class,
                    () -> processor.process(new BrandIngestMessage("59", "T")));
        }
    }

    private static BrandEntity existingBrand(long id, String fipe, String name) {
        BrandEntity b = new BrandEntity();
        b.id = id;
        b.fipeCode = fipe;
        b.name = name;
        b.createdAt = Instant.parse("2024-01-01T00:00:00Z");
        return b;
    }

    private static FipeModeloJson modelo(Integer codigo, String nome) {
        FipeModeloJson m = new FipeModeloJson();
        m.setCodigo(codigo);
        m.setNome(nome);
        return m;
    }

    private static FipeModelosResponseJson emptyModelos() {
        FipeModelosResponseJson r = new FipeModelosResponseJson();
        r.setModelos(List.of());
        return r;
    }

    private static FipeModelosResponseJson modelos(FipeModeloJson... items) {
        FipeModelosResponseJson r = new FipeModelosResponseJson();
        r.setModelos(List.of(items));
        return r;
    }
}
