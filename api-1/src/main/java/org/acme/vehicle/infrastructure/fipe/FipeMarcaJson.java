package org.acme.vehicle.infrastructure.fipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FipeMarcaJson(
        @JsonProperty("codigo") String codigo,
        @JsonProperty("nome") String nome) {}
