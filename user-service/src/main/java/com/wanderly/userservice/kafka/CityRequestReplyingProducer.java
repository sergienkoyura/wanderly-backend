package com.wanderly.userservice.kafka;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CityLookupRequest;
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
public class CityRequestReplyingProducer {
    private final ReplyingKafkaTemplate<String, CityLookupRequest, CityDto> kafkaTemplate;
    private final String topic = "geo.city.get-request";

    public CityDto requestCityDetails(UUID cityId) {
        CityLookupRequest request = new CityLookupRequest(cityId);
        ProducerRecord<String, CityLookupRequest> record =
                new ProducerRecord<>(topic, cityId.toString(), request);
        try {
            RequestReplyFuture<String, CityLookupRequest, CityDto> future =
                    kafkaTemplate.sendAndReceive(record);

            ConsumerRecord<String, CityDto> response = future.get(3, TimeUnit.SECONDS);
            return response.value();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch city info from geo-service", e);
        }
    }

}
