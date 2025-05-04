package com.wanderly.geoservice.service.impl;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CitySavedResponse;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.exception.CityNotFoundException;
import com.wanderly.geoservice.kafka.CityLookupProducer;
import com.wanderly.geoservice.mapper.CityMapper;
import com.wanderly.geoservice.repository.CityRepository;
import com.wanderly.geoservice.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    public CitySavedResponse save(CityDto cityDto) {
        City city = cityMapper.toCity(cityDto);

        City savedCity = cityRepository.findByOsmId(city.getOsmId())
                .orElseGet(() -> {
                    log.info("Creating new city: {}", city.getName());
                    return cityRepository.save(city);
                });

        return new CitySavedResponse(savedCity.getId());

//        cityLookupProducer.sendCityLookupResponse(
//                new CitySavedResponse(cityDto.getPreferencesId(), savedCity.getId())
//        );
    }

    @Override
    public CityDto findDtoById(UUID id) {
        City savedCity = cityRepository.findById(id)
                .orElseThrow(CityNotFoundException::new);
        return cityMapper.toCityLookupRequest(savedCity);
    }
}
