package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.Marker;

import java.util.List;
import java.util.UUID;

public interface MarkerService {
    List<MarkerDto> findAllDtosByCityId(UUID cityId);
    
    Marker findById(UUID markerId);

    int countAllByCityId(UUID cityId);

    List<Marker> findAllUnusedByCityIdAndUserId(UUID cityId, UUID userId);

    List<Marker> findAllUnusedByCityId(UUID cityId);

    List<Marker> findAllByCityId(UUID cityId);

    List<Marker> findAllByCityIdAndCategoryNature(UUID cityId);

    List<Marker> findAllUnusedByCityIdAndUserIdExceptRouteId(UUID cityId, UUID userId, UUID id);
}
