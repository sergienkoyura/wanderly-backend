package com.wanderly.userservice.configuration;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.common.dto.geo.CitySavedResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class CityKafkaConfiguration {
    // default kafka template
//    @Bean
//    public KafkaTemplate<String, CityDto> cityLookupKafkaTemplate(
//            ProducerFactory<String, CityDto> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }

    // replying kafka template
    @Bean
    public ReplyingKafkaTemplate<String, CityDto, CitySavedResponse> replyingPostCityKafkaTemplate(
            ProducerFactory<String, CityDto> pf,
            KafkaMessageListenerContainer<String, CitySavedResponse> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    // listening to the reply
    @Bean
    public KafkaMessageListenerContainer<String, CitySavedResponse> replyPostCityContainer(
            ConsumerFactory<String, CitySavedResponse> cf) {
        ContainerProperties containerProperties = new ContainerProperties("geo.city.post-response");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    // replying kafka template
    @Bean
    public ReplyingKafkaTemplate<String, CityLookupRequest, CityDto> replyingGetCityKafkaTemplate(
            ProducerFactory<String, CityLookupRequest> pf,
            KafkaMessageListenerContainer<String, CityDto> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    // listening to the reply
    @Bean
    public KafkaMessageListenerContainer<String, CityDto> replyGetCityContainer(
            ConsumerFactory<String, CityDto> cf) {
        ContainerProperties containerProperties = new ContainerProperties("geo.city.get-response");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }
}
