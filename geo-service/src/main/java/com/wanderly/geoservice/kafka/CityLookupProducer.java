package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.common.dto.geo.CityLookupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CityLookupProducer {
    private final KafkaTemplate<String, CityLookupResponse> kafkaTemplate;
    private final String topic = "city-lookup-response";

    public void sendCityLookupResponse(CityLookupResponse cityLookupResponse) {
        kafkaTemplate.send(topic, cityLookupResponse);
    }
}
