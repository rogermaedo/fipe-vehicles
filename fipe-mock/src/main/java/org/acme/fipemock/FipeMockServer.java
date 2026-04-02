package org.acme.fipemock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

/**
 * Simula a base usada pelos RestClients (equivalente a {@code https://…/fipe/api/v1} no {@code fipeapi.json}).
 * Paths: {@code /carros/marcas}, {@code /carros/marcas/{codigoMarca}/modelos} ({@code codigoMarca} string).
 *
 * <p>Corpos: {@link FipeContractResponses} (ficheiros {@code fipe-contract/*.json} conforme schemas do OpenAPI).
 *
 * <p>Porta padrão 8090. Sobrescrever com {@code FIPE_MOCK_PORT}.
 */
public final class FipeMockServer {

    private FipeMockServer() {}

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("FIPE_MOCK_PORT", "8090"));
        WireMockServer server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));

        server.stubFor(
                get(urlEqualTo("/carros/marcas"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(FipeContractResponses.marcasJson())));

        server.stubFor(
                get(urlPathMatching("/carros/marcas/[^/]+/modelos"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(FipeContractResponses.modelosResponseJson())));

        server.start();
        String url = "http://localhost:" + port;
        System.out.println("FIPE mock em " + url);
        System.out.println("Defina nas api-1 e api-2: FIPE_API_BASE_URL=" + url);
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    server.stop();
                                    System.out.println("FIPE mock encerrado.");
                                }));
    }
}
