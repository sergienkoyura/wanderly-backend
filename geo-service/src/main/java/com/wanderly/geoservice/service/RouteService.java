package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.entity.Route;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    List<RouteDto> findAllByCityIdAndUserId(UUID cityId, UUID userId);

    RouteDto generateByCityIdAndUserId(UUID cityId, UUID userId);
}
