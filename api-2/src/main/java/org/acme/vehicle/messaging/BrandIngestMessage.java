package org.acme.vehicle.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrandIngestMessage(
        @JsonProperty("fipeCode") String fipeCode,
        @JsonProperty("name") String name) {

    @Override
    public String toString() {
        return "BrandIngestMessage{fipeCode='" + fipeCode + "', name='" + name + "'}";
    }
}
