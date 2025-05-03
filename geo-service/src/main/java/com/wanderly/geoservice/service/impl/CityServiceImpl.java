package com.wanderly.geoservice.service.impl;

import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.common.dto.geo.CityLookupResponse;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.kafka.CityLookupProducer;
import com.wanderly.geoservice.mapper.CityMapper;
import com.wanderly.geoservice.repository.CityRepository;
import com.wanderly.geoservice.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CityLookupProducer cityLookupProducer;

    @Override
    public void save(CityLookupRequest cityLookupRequest) {
        City city = cityMapper.toCity(cityLookupRequest);

        City savedCity = cityRepository.findByPlaceId(city.getPlaceId())
                .orElseGet(() -> {
                    log.info("Creating new city: {}", city.getName());
                    return cityRepository.save(city);
                });

        cityLookupProducer.sendCityLookupResponse(
                new CityLookupResponse(cityLookupRequest.getPreferencesId(), savedCity.getId())
        );
    }
}
