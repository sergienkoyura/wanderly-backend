package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.UserARModelCompletionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserARModelCompletionProducer {
    private final KafkaTemplate<String, UserARModelCompletionMessage> kafkaTemplate;
    private static final String topic = "user.ar-model.complete";

    public void sendCompletionMessage(UserARModelCompletionMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
