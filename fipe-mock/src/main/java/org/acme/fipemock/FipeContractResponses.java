package org.acme.fipemock;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Corpos HTTP alinhados a {@code fipeapi.json} (OpenAPI 3) para as operações usadas pelas apps:
 * <ul>
 *   <li>{@code GET /carros/marcas} → array de {@code Marca}
 *   <li>{@code GET /carros/marcas/{codigoMarca}/modelos} → {@code ModelosResponse}
 * </ul>
 *
 * <p>Ficheiros em {@code classpath:/fipe-contract/} — devem manter-se consistentes com
 * {@code components.schemas.Marca}, {@code Modelo} e {@code ModelosResponse}.
 */
public final class FipeContractResponses {

    static final String MARCAS_RESOURCE = "fipe-contract/marcas.json";
    static final String MODELOS_RESOURCE = "fipe-contract/modelos-response.json";

    private FipeContractResponses() {}

    public static String marcasJson() {
        return readUtf8(MARCAS_RESOURCE);
    }

    public static String modelosResponseJson() {
        return readUtf8(MODELOS_RESOURCE);
    }

    static String readUtf8(String resourcePath) {
        ClassLoader cl = FipeContractResponses.class.getClassLoader();
        InputStream in = Objects.requireNonNull(cl.getResourceAsStream(resourcePath), () -> resourcePath);
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
