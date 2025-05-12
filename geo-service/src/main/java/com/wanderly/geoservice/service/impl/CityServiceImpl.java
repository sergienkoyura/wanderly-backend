package com.wanderly.geoservice.service.impl;

import com.wanderly.geoservice.dto.CityDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.exception.CityNotFoundException;
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
    public City findById(UUID id) {
        return cityRepository.findById(id)
                .orElseThrow(CityNotFoundException::new);
    }

    @Override
    public City findByOsmId(CityDto cityDto) {

        return cityRepository.findByOsmId(cityDto.getOsmId())
                .orElseGet(() -> {
                    log.info("Creating new city: {}", cityDto.getName());
                    City city = cityMapper.toCity(cityDto);
                    return cityRepository.save(city);
                });
    }
}
