package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.common.dto.geo.CitySavedResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CitySaveReplyingProducer {
    private final ReplyingKafkaTemplate<String, CityDto, CitySavedResponse> kafkaTemplate;
    private final String topic = "geo.city.post-request";

    public CitySavedResponse saveCity(UUID userId, CityDto cityDto) {
        ProducerRecord<String, CityDto> record =
                new ProducerRecord<>(topic, userId.toString(), cityDto);
        try {
            RequestReplyFuture<String, CityDto, CitySavedResponse> future =
                    kafkaTemplate.sendAndReceive(record);

            ConsumerRecord<String, CitySavedResponse> response = future.get(3, TimeUnit.SECONDS);
            return response.value();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save city in the geo-service", e);
        }
    }
}