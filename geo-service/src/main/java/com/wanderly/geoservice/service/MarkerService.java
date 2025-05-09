package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.Marker;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MarkerService {
    List<MarkerDto> findAllByCityId(UUID cityId);
}
