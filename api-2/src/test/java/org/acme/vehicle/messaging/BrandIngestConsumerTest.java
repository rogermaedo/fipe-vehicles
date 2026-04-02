package org.acme.vehicle.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.vehicle.application.BrandIngestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandIngestConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    BrandIngestProcessor brandIngestProcessor;

    BrandIngestConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new BrandIngestConsumer(objectMapper, brandIngestProcessor);
    }

    @Test
    void consume_validJson_delegatesToProcessor() throws Exception {
        String json = "{\"fipeCode\":\"59\",\"name\":\"Toyota\"}";

        consumer.consume(json);

        verify(brandIngestProcessor)
                .process(
                        argThat(
                                m ->
                                        "59".equals(m.fipeCode())
                                                && "Toyota".equals(m.name())));
    }

    @Test
    void consume_invalidJson_wrapsRuntimeException() {
        assertThrows(RuntimeException.class, () -> consumer.consume("not-json"));

        verifyNoInteractions(brandIngestProcessor);
    }

    @Test
    void consume_processorFailure_propagates() {
        doThrow(new RuntimeException("db")).when(brandIngestProcessor).process(any());

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> consumer.consume("{\"fipeCode\":\"1\",\"name\":\"A\"}"));

        assertEquals("db", ex.getCause().getMessage());
    }
}
