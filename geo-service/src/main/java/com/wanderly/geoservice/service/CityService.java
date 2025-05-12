package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.CityDto;
import com.wanderly.geoservice.entity.City;

import java.util.UUID;

public interface CityService {
    City findById(UUID id);

    City findByOsmId(CityDto cityDto);
}
