package org.acme.vehicle.infrastructure.fipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.acme.vehicle.application.exception.FipeCatalogUnavailableException;
import org.acme.vehicle.domain.model.FipeBrand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FipeBrandsCatalogAdapterTest {

    @Mock
    FipeApiClient client;

    @InjectMocks
    FipeBrandsCatalogAdapter adapter;

    @Test
    void fetchAll_mapsMarcasToDomain() {
        when(client.listMarcas())
                .thenReturn(
                        List.of(
                                new FipeMarcaJson("59", "Toyota"),
                                new FipeMarcaJson("60", "Volkswagen")));

        List<FipeBrand> brands = adapter.fetchAll();

        assertEquals(2, brands.size());
        assertEquals(new FipeBrand("59", "Toyota"), brands.get(0));
        assertEquals(new FipeBrand("60", "Volkswagen"), brands.get(1));
    }

    @Test
    void fetchAll_webApplicationException_wrapsAsCatalogUnavailable() {
        when(client.listMarcas()).thenThrow(new WebApplicationException(502));

        FipeCatalogUnavailableException ex =
                assertThrows(FipeCatalogUnavailableException.class, () -> adapter.fetchAll());

        assertEquals("A API FIPE retornou erro HTTP.", ex.getMessage());
    }

    @Test
    void fetchAll_processingException_wrapsAsCatalogUnavailable() {
        when(client.listMarcas()).thenThrow(new ProcessingException("timeout"));

        FipeCatalogUnavailableException ex =
                assertThrows(FipeCatalogUnavailableException.class, () -> adapter.fetchAll());

        assertEquals("Falha ao contactar a API FIPE (rede ou timeout).", ex.getMessage());
    }
}
