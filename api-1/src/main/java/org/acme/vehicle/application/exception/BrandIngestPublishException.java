package org.acme.vehicle.application.exception;

/**
 * Falha ao publicar marcas na fila (serialização ou broker). Mapeada para HTTP 503 na API.
 */
public class BrandIngestPublishException extends RuntimeException {

    public BrandIngestPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
