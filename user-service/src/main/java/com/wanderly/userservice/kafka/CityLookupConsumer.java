package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.geo.CityLookupResponse;
import com.wanderly.userservice.entity.UserPreferences;
import com.wanderly.userservice.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityLookupConsumer {
    private final UserPreferencesService userPreferencesService;

    @KafkaListener(topics = "city-lookup-response", groupId = "user-service-group")
    public void listen(ConsumerRecord<String, CityLookupResponse> record, Acknowledgment ack) {
        log.info("Received: {}", record.value());
        ack.acknowledge();

        UserPreferences preferences = userPreferencesService.findById(record.value().getPreferencesId());
        preferences.setCityId(record.value().getCityId());
        userPreferencesService.save(preferences);

        log.info("Saved city {} for a user", preferences.getUserId());
    }
}