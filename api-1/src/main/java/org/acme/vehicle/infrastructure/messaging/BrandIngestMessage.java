package org.acme.vehicle.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload publicado na fila (uma mensagem por marca) para a api-2 processar modelos.
 */
public record BrandIngestMessage(
        @JsonProperty("fipeCode") String fipeCode,
        @JsonProperty("name") String name) {}
