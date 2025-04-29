package com.wanderly.authservice.kafka;

import com.wanderly.common.dto.VerificationEmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEmailProducer {
    private final KafkaTemplate<String, VerificationEmailMessage> kafkaTemplate;
    private final String topic = "verification-email-topic";

    public void sendVerificationEmail(VerificationEmailMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
