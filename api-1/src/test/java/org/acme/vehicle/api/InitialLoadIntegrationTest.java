package org.acme.vehicle.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.acme.vehicle.domain.model.FipeBrand;
import org.acme.vehicle.infrastructure.messaging.RabbitBrandIngestPublisher;
import org.acme.vehicle.support.FipeWireMockResource;
import org.junit.jupiter.api.Test;

/**
 * Integração: HTTP → caso de uso → cliente FIPE (WireMock) → publicação na fila (in-memory no perfil test).
 * <p>
 * Não substitui validação automática byte-a-byte contra {@code META-INF/openapi.yaml}; para isso use
 * contract test (ex.: openapi-diff, Pact ou swagger-request-validator em pipeline CI).
 */
@QuarkusTest
@QuarkusTestResource(value = FipeWireMockResource.class, restrictToAnnotatedClass = true)
class InitialLoadIntegrationTest {

    @InjectSpy
    RabbitBrandIngestPublisher rabbitBrandIngestPublisher;

    @Test
    void postInitialLoad_returns202AndEnqueuesOneMessagePerBrandFromFipe() {
        given().when()
                .post("/api/v1/initial-load")
                .then()
                .statusCode(202)
                .contentType("application/json")
                .body("brandsEnqueued", equalTo(2))
                .body("brandsEnqueued", notNullValue());

        verify(rabbitBrandIngestPublisher)
                .publishAll(
                        argThat(
                                (List<FipeBrand> brands) ->
                                        brands.size() == 2
                                                && brands.contains(new FipeBrand("59", "Toyota"))
                                                && brands.contains(new FipeBrand("60", "Volkswagen"))));
    }
}
