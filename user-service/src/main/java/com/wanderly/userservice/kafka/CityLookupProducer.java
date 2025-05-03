package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.geo.CityLookupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CityLookupProducer {
    private final KafkaTemplate<String, CityLookupRequest> kafkaTemplate;
    private final String topic = "city-lookup-request";

    public void sendCityLookupRequest(CityLookupRequest cityLookupRequest) {
        kafkaTemplate.send(topic, cityLookupRequest);
    }
}
