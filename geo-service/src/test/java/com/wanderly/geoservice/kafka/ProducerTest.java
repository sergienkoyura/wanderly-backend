package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.EraseRouteProgressMessage;
import com.wanderly.common.dto.UserARModelCompletionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProducerTest {

    private KafkaTemplate<String, EraseRouteProgressMessage> eraseTemplate;
    private KafkaTemplate<String, UserARModelCompletionMessage> completionTemplate;

    private EraseRouteProgressProducer eraseProducer;
    private UserARModelCompletionProducer completionProducer;

    @BeforeEach
    void setup() {
        eraseTemplate = mock(KafkaTemplate.class);
        completionTemplate = mock(KafkaTemplate.class);

        eraseProducer = new EraseRouteProgressProducer(eraseTemplate);
        completionProducer = new UserARModelCompletionProducer(completionTemplate);
    }

    @Test
    void sendProgressEraseMessage_sendsCorrectKafkaMessage() {
        EraseRouteProgressMessage message = new EraseRouteProgressMessage();
        eraseProducer.sendProgressEraseMessage(message);

        ArgumentCaptor<EraseRouteProgressMessage> captor = ArgumentCaptor.forClass(EraseRouteProgressMessage.class);
        verify(eraseTemplate, times(1)).send(eq("user.route.erase-progress"), captor.capture());

        assertThat(captor.getValue()).isEqualTo(message);
    }

    @Test
    void sendCompletionMessage_sendsCorrectKafkaMessage() {
        UserARModelCompletionMessage message = new UserARModelCompletionMessage();
        completionProducer.sendCompletionMessage(message);

        ArgumentCaptor<UserARModelCompletionMessage> captor = ArgumentCaptor.forClass(UserARModelCompletionMessage.class);
        verify(completionTemplate, times(1)).send(eq("user.ar-model.complete"), captor.capture());

        assertThat(captor.getValue()).isEqualTo(message);
    }
}
