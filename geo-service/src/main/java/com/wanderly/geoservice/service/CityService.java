package com.wanderly.geoservice.service;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.common.dto.geo.CitySavedResponse;

import java.util.UUID;

public interface CityService {
    CitySavedResponse save(CityDto cityDto);

    CityDto findDtoById(UUID id);
}
