package org.acme.vehicle.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.acme.vehicle.application.exception.FipeCatalogUnavailableException;
import org.acme.vehicle.application.port.BrandIngestPublisher;
import org.acme.vehicle.application.port.FipeBrandsCatalog;
import org.acme.vehicle.domain.model.FipeBrand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InitialLoadApplicationServiceTest {

    @Mock
    FipeBrandsCatalog fipeBrandsCatalog;

    @Mock
    BrandIngestPublisher brandIngestPublisher;

    @InjectMocks
    InitialLoadApplicationService service;

    @Test
    void enqueuesOneMessagePerBrand() {
        var brands =
                List.of(new FipeBrand("59", "Toyota"), new FipeBrand("60", "Volkswagen"));
        when(fipeBrandsCatalog.fetchAll()).thenReturn(brands);

        assertEquals(2, service.executeInitialLoad());

        verify(brandIngestPublisher).publishAll(brands);
    }

    @Test
    void emptyFipeListStillSucceeds() {
        when(fipeBrandsCatalog.fetchAll()).thenReturn(List.of());

        assertEquals(0, service.executeInitialLoad());

        verify(brandIngestPublisher).publishAll(List.of());
    }

    @Test
    void fipeFailure_doesNotPublish() {
        when(fipeBrandsCatalog.fetchAll())
                .thenThrow(new FipeCatalogUnavailableException("down", new RuntimeException()));

        assertThrows(FipeCatalogUnavailableException.class, () -> service.executeInitialLoad());

        verifyNoInteractions(brandIngestPublisher);
    }
}
