package com.wanderly.geoservice.service.impl;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.mapper.MarkerMapper;
import com.wanderly.geoservice.repository.MarkerRepository;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.MarkerService;
import com.wanderly.geoservice.util.OSMUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {
    private final MarkerRepository markerRepository;
    private final CityService cityService;
    private final MarkerMapper markerMapper;

    @Override
    public List<MarkerDto> findAllByCityId(UUID cityId) {
        List<Marker> markers = markerRepository.findAllByCityId(cityId);
        if (!markers.isEmpty()) {
            return markerMapper.toDtos(markers);
        }

        // fetching city for validation
        City city = cityService.findById(cityId);
        List<Marker> fetchedMarkers = OSMUtil.fetchMarkers(city);
        List<Marker> savedMarkers = markerRepository.saveAll(fetchedMarkers);
        return markerMapper.toDtos(savedMarkers);
    }
}
