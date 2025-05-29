package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.EraseRouteProgressMessage;
import com.wanderly.common.dto.UserARModelCompletionMessage;
import com.wanderly.userservice.service.UserARModelCompletionService;
import com.wanderly.userservice.service.UserRouteCompletionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConsumerTest {

    private UserRouteCompletionService userRouteCompletionService;
    private UserARModelCompletionService userARModelCompletionService;

    private EraseRouteProgressConsumer eraseRouteProgressConsumer;
    private UserARModelCompletionConsumer userARModelCompletionConsumer;

    @BeforeEach
    void setup() {
        userRouteCompletionService = mock(UserRouteCompletionService.class);
        userARModelCompletionService = mock(UserARModelCompletionService.class);

        eraseRouteProgressConsumer = new EraseRouteProgressConsumer(userRouteCompletionService);
        userARModelCompletionConsumer = new UserARModelCompletionConsumer(userARModelCompletionService);
    }

    @Test
    void eraseRouteProgressConsumer_shouldAcknowledgeAndCallService() {
        UUID routeId = UUID.randomUUID();
        EraseRouteProgressMessage message = new EraseRouteProgressMessage(routeId);
        ConsumerRecord<String, EraseRouteProgressMessage> consumerRecord = new ConsumerRecord<>("topic", 0, 0L, "key", message);
        Acknowledgment ack = mock(Acknowledgment.class);

        eraseRouteProgressConsumer.listen(consumerRecord, ack);

        verify(ack).acknowledge();
        verify(userRouteCompletionService).eraseProgressByRouteId(routeId);
    }

    @Test
    void userARModelCompletionConsumer_shouldAcknowledgeAndCallService() {
        UUID userId = UUID.randomUUID();
        UUID modelId = UUID.randomUUID();
        String cityName = "Kyiv";

        UserARModelCompletionMessage message = new UserARModelCompletionMessage(userId, modelId, cityName);
        ConsumerRecord<String, UserARModelCompletionMessage> consumerRecord = new ConsumerRecord<>("topic", 0, 0L, "key", message);
        Acknowledgment ack = mock(Acknowledgment.class);

        userARModelCompletionConsumer.listen(consumerRecord, ack);

        verify(ack).acknowledge();
        verify(userARModelCompletionService).save(userId, modelId, cityName);
    }
}
