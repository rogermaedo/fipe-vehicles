package org.acme.vehicle.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.acme.vehicle.application.exception.BrandIngestPublishException;
import org.acme.vehicle.application.port.BrandIngestPublisher;
import org.acme.vehicle.domain.model.FipeBrand;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;
import io.smallrye.reactive.messaging.MutinyEmitter;

@ApplicationScoped
public class RabbitBrandIngestPublisher implements BrandIngestPublisher {

    private static final Logger LOG = Logger.getLogger(RabbitBrandIngestPublisher.class);

    private final MutinyEmitter<String> emitter;
    private final ObjectMapper objectMapper;

    @Inject
    public RabbitBrandIngestPublisher(@Channel("brands-out") MutinyEmitter<String> emitter, ObjectMapper objectMapper) {
        this.emitter = emitter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishAll(List<FipeBrand> brands) {
        for (FipeBrand brand : brands) {
            try {
                BrandIngestMessage payload = new BrandIngestMessage(brand.fipeCode(), brand.name());
                String json = objectMapper.writeValueAsString(payload);
                emitter.sendAndAwait(json);
                LOG.infof("Mensagem enfileirada na fila (brands-out / exchange vehicle-brands): %s", payload);
            } catch (JsonProcessingException e) {
                throw new BrandIngestPublishException("Falha ao serializar mensagem para a fila.", e);
            } catch (RuntimeException e) {
                throw new BrandIngestPublishException("Falha ao publicar marca na fila.", e);
            }
        }
    }
}
