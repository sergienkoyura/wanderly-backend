package com.wanderly.geoservice.service.impl;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.exception.MarkerNotFoundException;
import com.wanderly.geoservice.mapper.MarkerMapper;
import com.wanderly.geoservice.repository.MarkerRepository;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.MarkerService;
import com.wanderly.geoservice.util.OSMUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {
    private final MarkerRepository markerRepository;
    private final CityService cityService;
    private final MarkerMapper markerMapper;

    @Cacheable(value = "cityMarkers", key = "#root.args[0]")
    @Override
    public List<MarkerDto> findAllDtosByCityId(UUID cityId) {
        List<Marker> markers = markerRepository.findAllByCityId(cityId);
        if (!markers.isEmpty()) {
            return markerMapper.toDtos(markers);
        }

        City city = cityService.findById(cityId);
        List<Marker> fetchedMarkers = OSMUtil.fetchMarkers(city);
        List<Marker> savedMarkers = markerRepository.saveAll(fetchedMarkers);
        return markerMapper.toDtos(savedMarkers);
    }

    @Override
    public Marker findById(UUID markerId) {
        return markerRepository.findById(markerId)
                .orElseThrow(MarkerNotFoundException::new);
    }

    @Override
    public int countAllByCityId(UUID cityId) {
        return markerRepository.countAllByCityId(cityId);
    }

    @Override
    public List<Marker> findAllUnusedByCityIdAndUserId(UUID cityId, UUID userId) {
        return markerRepository.findAllUnusedByCityIdAndUserId(cityId, userId);
    }

    @Override
    public List<Marker> findAllUnusedByCityId(UUID cityId) {
        return markerRepository.findAllUnusedByCityId(cityId);
    }

    @Override
    public List<Marker> findAllByCityId(UUID cityId) {
        return markerRepository.findAllByCityId(cityId);
    }

    @Override
    public List<Marker> findAllByCityIdAndCategoryNature(UUID cityId) {
        return markerRepository.findAllByCityIdAndCategory(cityId, MarkerCategory.NATURE);
    }

    @Override
    public List<Marker> findAllUnusedByCityIdAndUserIdExceptRouteId(UUID cityId, UUID userId, UUID routeId) {
        return markerRepository.findAllUnusedByCityIdAndUserIdExceptRouteId(cityId, userId, routeId);
    }
}
