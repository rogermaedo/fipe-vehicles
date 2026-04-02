package org.acme.vehicle.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.vehicle.application.InitialLoadApplicationService;
import org.junit.jupiter.api.Test;

@QuarkusTest
class InitialLoadResourceTest {

    @InjectMock
    InitialLoadApplicationService initialLoadApplicationService;

    @Test
    void postReturns202WithCount() {
        when(initialLoadApplicationService.executeInitialLoad()).thenReturn(3);

        given().when()
                .post("/api/v1/initial-load")
                .then()
                .statusCode(202)
                .body("brandsEnqueued", is(3));
    }
}
