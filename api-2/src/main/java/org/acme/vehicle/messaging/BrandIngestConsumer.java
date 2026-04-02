package org.acme.vehicle.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.vehicle.application.BrandIngestProcessor;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
public class BrandIngestConsumer {

    private static final Logger LOG = Logger.getLogger(BrandIngestConsumer.class);

    private final ObjectMapper objectMapper;
    private final BrandIngestProcessor brandIngestProcessor;

    @Inject
    public BrandIngestConsumer(ObjectMapper objectMapper, BrandIngestProcessor brandIngestProcessor) {
        this.objectMapper = objectMapper;
        this.brandIngestProcessor = brandIngestProcessor;
    }

    @Incoming("brands-in")
    @Blocking
    public void consume(String json) {
        try {
            BrandIngestMessage msg = objectMapper.readValue(json, BrandIngestMessage.class);
            LOG.infof("Mensagem consumida da fila (brands-in / queue vehicle-brands-api2): %s", msg);
            brandIngestProcessor.process(msg);
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao processar mensagem de marca: %s", json);
            throw new RuntimeException(e);
        }
    }
}
