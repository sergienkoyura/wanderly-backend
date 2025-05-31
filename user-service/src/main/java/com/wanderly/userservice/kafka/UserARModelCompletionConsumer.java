package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.UserARModelCompletionMessage;
import com.wanderly.userservice.service.UserARModelCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserARModelCompletionConsumer {
    private final UserARModelCompletionService userARModelCompletionService;

    @KafkaListener(topics = "user.ar-model.complete", groupId = "user-service-group")
    public void listen(ConsumerRecord<String, UserARModelCompletionMessage> consumerRecord, Acknowledgment ack) {
        log.info("Received: {}", consumerRecord.value());
        ack.acknowledge();

        userARModelCompletionService.save(consumerRecord.value().getUserId(), consumerRecord.value().getModelId(), consumerRecord.value().getCityName());
    }

}
