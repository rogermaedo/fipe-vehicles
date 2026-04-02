package org.acme.vehicle.support;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Mock HTTP com os mesmos corpos que o módulo {@code fipe-mock} ({@code fipeapi.json} — {@code Marca},
 * {@code ModelosResponse}).
 */
public class FipeWireMockResource implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        server.start();

        server.stubFor(
                get(urlPathEqualTo("/carros/marcas"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(readUtf8("fipe-contract/marcas.json"))));

        server.stubFor(
                get(urlPathMatching("/carros/marcas/[^/]+/modelos"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(readUtf8("fipe-contract/modelos-response.json"))));

        String baseUrl = "http://localhost:" + server.port();
        return Map.of("quarkus.rest-client.fipe-api.url", baseUrl);
    }

    private String readUtf8(String resourcePath) {
        InputStream in =
                Objects.requireNonNull(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath),
                        () -> "Resource not found: " + resourcePath);
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
