package org.acme.vehicle.contract.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    private String details;

    public ErrorResponse() {}

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("details")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
