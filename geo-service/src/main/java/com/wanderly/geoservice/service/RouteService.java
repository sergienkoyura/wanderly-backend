package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.BranchRequest;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.entity.Route;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    List<RouteDto> findAllByCityIdAndUserId(UUID cityId, UUID userId);

    RouteDto generateByCityIdAndUserId(UUID cityId, UUID userId);

    void deleteByRouteIdAndUserId(UUID routeId, UUID userId);

    RouteDto save(UUID userId, RouteDto routeDto);

    RouteDto branch(UUID userId, BranchRequest branchRequest);
}
