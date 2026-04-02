package org.acme.vehicle.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.MutinyEmitter;
import java.util.List;
import org.acme.vehicle.application.exception.BrandIngestPublishException;
import org.acme.vehicle.domain.model.FipeBrand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RabbitBrandIngestPublisherTest {

    @Mock
    MutinyEmitter<String> emitter;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    RabbitBrandIngestPublisher publisher;

    @Test
    void publishAll_serializesAndSendsEachBrand() throws Exception {
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any(BrandIngestMessage.class)))
                .thenReturn("{\"a\":1}")
                .thenReturn("{\"b\":2}");

        publisher.publishAll(
                List.of(new FipeBrand("59", "Toyota"), new FipeBrand("60", "Volkswagen")));

        verify(emitter).sendAndAwait("{\"a\":1}");
        verify(emitter).sendAndAwait("{\"b\":2}");
        verify(objectMapper, times(2)).writeValueAsString(org.mockito.ArgumentMatchers.any(BrandIngestMessage.class));
    }

    @Test
    void publishAll_jsonFailure_wrapsBrandIngestPublishException() throws Exception {
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any(BrandIngestMessage.class)))
                .thenThrow(
                        new JsonProcessingException("serialization") {
                            private static final long serialVersionUID = 1L;
                        });

        assertThrows(
                BrandIngestPublishException.class,
                () -> publisher.publishAll(List.of(new FipeBrand("1", "A"))));
    }

    @Test
    void publishAll_emitterFailure_wrapsBrandIngestPublishException() throws Exception {
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any(BrandIngestMessage.class)))
                .thenReturn("{}");
        doThrow(new RuntimeException("broker down")).when(emitter).sendAndAwait(anyString());

        assertThrows(
                BrandIngestPublishException.class,
                () -> publisher.publishAll(List.of(new FipeBrand("1", "A"))));
    }
}
