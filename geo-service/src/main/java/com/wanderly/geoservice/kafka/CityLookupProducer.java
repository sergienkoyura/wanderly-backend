package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.geo.CitySavedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CityLookupProducer {
    private final KafkaTemplate<String, CitySavedResponse> kafkaTemplate;
    private final String topic = "city-lookup-response";

    public void sendCityLookupResponse(CitySavedResponse citySavedResponse) {
        kafkaTemplate.send(topic, citySavedResponse);
    }
}
