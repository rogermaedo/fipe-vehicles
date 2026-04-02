package org.acme.vehicle.contract.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrandResponse {

    private Long id;
    private String fipeCode;
    private String name;

    public BrandResponse() {}

    public BrandResponse(Long id, String fipeCode, String name) {
        this.id = id;
        this.fipeCode = fipeCode;
        this.name = name;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("fipeCode")
    public String getFipeCode() {
        return fipeCode;
    }

    public void setFipeCode(String fipeCode) {
        this.fipeCode = fipeCode;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
