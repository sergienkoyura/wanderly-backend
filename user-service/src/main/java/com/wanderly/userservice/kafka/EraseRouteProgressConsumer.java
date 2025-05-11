package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.EraseRouteProgressMessage;
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
public class EraseRouteProgressConsumer {
    private final UserRouteCompletionService userRouteCompletionService;

    @KafkaListener(topics = "user.route.erase-progress", groupId = "user-service-group")
    public void listen(ConsumerRecord<String, EraseRouteProgressMessage> record, Acknowledgment ack) {
        log.info("Received: {}", record.value());
        ack.acknowledge();

        userRouteCompletionService.eraseProgressByRouteId(record.value().getRouteId());
    }

}
