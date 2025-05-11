package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.EraseRouteProgressMessage;
import com.wanderly.common.dto.UserARModelCompletionMessage;
import com.wanderly.userservice.service.UserARModelCompletionService;
import com.wanderly.userservice.service.UserRouteCompletionService;
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
    public void listen(ConsumerRecord<String, UserARModelCompletionMessage> record, Acknowledgment ack) {
        log.info("Received: {}", record.value());
        ack.acknowledge();

        userARModelCompletionService.save(record.value().getUserId(), record.value().getModelId());
    }

}
