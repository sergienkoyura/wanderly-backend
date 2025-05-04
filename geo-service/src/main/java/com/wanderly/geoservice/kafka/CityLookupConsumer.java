package com.wanderly.geoservice.kafka;

import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CitySavedResponse;
import com.wanderly.geoservice.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityLookupConsumer {

    private final CityService cityService;

//    @KafkaListener(topics = "city-lookup-request", groupId = "geo-service-group")
//    public void listen(ConsumerRecord<String, CityDto> record, Acknowledgment ack) {
//        log.info("Received: {}", record.value());
//        ack.acknowledge();
//
//        // if city exists by place id ? save : create
//        cityService.save(record.value());
//    }

    @KafkaListener(topics = "geo.city.get-request")
    @SendTo("geo.city.get-response")
    public CityDto handleCityLookup(CityLookupRequest request) {
        return cityService.findDtoById(request.getId());
    }

    @KafkaListener(topics = "geo.city.post-request")
    @SendTo("geo.city.post-response")
    public CitySavedResponse handleCitySave(CityDto cityDto) {
        return cityService.save(cityDto);
    }

}