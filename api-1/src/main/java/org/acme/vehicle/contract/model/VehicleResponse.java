package org.acme.vehicle.contract.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleResponse {

    private Long id;
    private Long brandId;
    private Integer fipeModelCode;
    private String modelName;
    private String notes;
    private OffsetDateTime createdAt;

    public VehicleResponse() {}

    public VehicleResponse(
            Long id, Long brandId, Integer fipeModelCode, String modelName, String notes, OffsetDateTime createdAt) {
        this.id = id;
        this.brandId = brandId;
        this.fipeModelCode = fipeModelCode;
        this.modelName = modelName;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("brandId")
    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    @JsonProperty("fipeModelCode")
    public Integer getFipeModelCode() {
        return fipeModelCode;
    }

    public void setFipeModelCode(Integer fipeModelCode) {
        this.fipeModelCode = fipeModelCode;
    }

    @JsonProperty("modelName")
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("createdAt")
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
