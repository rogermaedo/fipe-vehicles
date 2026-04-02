package org.acme.vehicle.application.exception;

/**
 * Falha ao obter catálogo de marcas na FIPE (HTTP de erro, rede, timeout). Mapeada para HTTP 502 na API.
 */
public class FipeCatalogUnavailableException extends RuntimeException {

    public FipeCatalogUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
