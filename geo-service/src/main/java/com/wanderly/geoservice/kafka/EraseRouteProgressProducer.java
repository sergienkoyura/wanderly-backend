package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.EraseRouteProgressMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EraseRouteProgressProducer {
    private final KafkaTemplate<String, EraseRouteProgressMessage> kafkaTemplate;
    private final String topic = "user.route.erase-progress";

    public void sendProgressEraseMessage(EraseRouteProgressMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
