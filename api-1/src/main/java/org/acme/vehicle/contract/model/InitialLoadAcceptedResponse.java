package org.acme.vehicle.contract.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitialLoadAcceptedResponse {

    private Integer brandsEnqueued;
    private String correlationId;

    public InitialLoadAcceptedResponse() {}

    public InitialLoadAcceptedResponse(Integer brandsEnqueued) {
        this.brandsEnqueued = brandsEnqueued;
    }

    @JsonProperty("brandsEnqueued")
    public Integer getBrandsEnqueued() {
        return brandsEnqueued;
    }

    public void setBrandsEnqueued(Integer brandsEnqueued) {
        this.brandsEnqueued = brandsEnqueued;
    }

    @JsonProperty("correlationId")
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
