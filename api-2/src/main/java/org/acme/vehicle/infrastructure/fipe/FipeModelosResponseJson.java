package org.acme.vehicle.infrastructure.fipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FipeModelosResponseJson {

    private List<FipeModeloJson> modelos = new ArrayList<>();

    @JsonProperty("modelos")
    public List<FipeModeloJson> getModelos() {
        return modelos;
    }

    public void setModelos(List<FipeModeloJson> modelos) {
        this.modelos = modelos != null ? modelos : new ArrayList<>();
    }
}
