package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.exception.MarkerNotFoundException;
import com.wanderly.geoservice.mapper.MarkerMapper;
import com.wanderly.geoservice.repository.MarkerRepository;
import com.wanderly.geoservice.service.impl.MarkerServiceImpl;
import com.wanderly.geoservice.util.OSMUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkerServiceTest {

    @Mock private MarkerRepository markerRepository;
    @Mock private CityService cityService;
    @Mock private MarkerMapper markerMapper;
    @InjectMocks private MarkerServiceImpl markerService;

    @Test
    void findAllDtosByCityId_ReturnsMappedMarkers_WhenMarkersExist() {
        UUID cityId = UUID.randomUUID();
        List<Marker> markers = List.of(new Marker());
        List<MarkerDto> dtos = List.of(new MarkerDto());

        when(markerRepository.findAllByCityId(cityId)).thenReturn(markers);
        when(markerMapper.toDtos(markers)).thenReturn(dtos);

        List<MarkerDto> result = markerService.findAllDtosByCityId(cityId);

        assertThat(result).isEqualTo(dtos);
    }

    @Test
    void findAllDtosByCityId_FetchesFromOSM_WhenMarkersEmpty() {
        UUID cityId = UUID.randomUUID();
        City city = new City();
        List<Marker> fetchedMarkers = List.of(new Marker());
        List<MarkerDto> dtos = List.of(new MarkerDto());

        when(markerRepository.findAllByCityId(cityId)).thenReturn(Collections.emptyList());
        when(cityService.findById(cityId)).thenReturn(city);
        when(markerRepository.saveAll(any())).thenReturn(fetchedMarkers);
        when(markerMapper.toDtos(fetchedMarkers)).thenReturn(dtos);

        try (MockedStatic<OSMUtil> utilities = mockStatic(OSMUtil.class)) {
            utilities.when(() -> OSMUtil.fetchMarkers(city)).thenReturn(fetchedMarkers);

            List<MarkerDto> result = markerService.findAllDtosByCityId(cityId);

            assertThat(result).isEqualTo(dtos);
        }
    }

    @Test
    void findById_ReturnsMarker_WhenExists() {
        UUID markerId = UUID.randomUUID();
        Marker marker = new Marker();

        when(markerRepository.findById(markerId)).thenReturn(Optional.of(marker));

        Marker result = markerService.findById(markerId);
        assertThat(result).isEqualTo(marker);
    }

    @Test
    void findById_ThrowsException_WhenNotFound() {
        UUID markerId = UUID.randomUUID();

        when(markerRepository.findById(markerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> markerService.findById(markerId))
                .isInstanceOf(MarkerNotFoundException.class);
    }

    @Test
    void countAllByCityId_ReturnsCount() {
        UUID cityId = UUID.randomUUID();
        when(markerRepository.countAllByCityId(cityId)).thenReturn(10);

        int result = markerService.countAllByCityId(cityId);
        assertThat(result).isEqualTo(10);
    }

    @Test
    void findAllByCityIdAndCategoryNature_ReturnsList() {
        UUID cityId = UUID.randomUUID();
        List<Marker> markers = List.of(new Marker());

        when(markerRepository.findAllByCityIdAndCategory(cityId, MarkerCategory.NATURE)).thenReturn(markers);

        List<Marker> result = markerService.findAllByCityIdAndCategoryNature(cityId);
        assertThat(result).isEqualTo(markers);
    }

    @Test
    void findAllUnusedByCityId_ReturnsList() {
        UUID cityId = UUID.randomUUID();
        List<Marker> expectedMarkers = List.of(new Marker());

        when(markerRepository.findAllUnusedByCityId(cityId)).thenReturn(expectedMarkers);

        List<Marker> result = markerService.findAllUnusedByCityId(cityId);

        assertThat(result).isEqualTo(expectedMarkers);
    }

    @Test
    void findAllByCityId_ReturnsList() {
        UUID cityId = UUID.randomUUID();
        List<Marker> expectedMarkers = List.of(new Marker());

        when(markerRepository.findAllByCityId(cityId)).thenReturn(expectedMarkers);

        List<Marker> result = markerService.findAllByCityId(cityId);

        assertThat(result).isEqualTo(expectedMarkers);
    }

    @Test
    void findAllUnusedByCityIdAndUserIdExceptRouteId_ReturnsList() {
        UUID cityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();
        List<Marker> expectedMarkers = List.of(new Marker());

        when(markerRepository.findAllUnusedByCityIdAndUserIdExceptRouteId(cityId, userId, routeId))
                .thenReturn(expectedMarkers);

        List<Marker> result = markerService.findAllUnusedByCityIdAndUserIdExceptRouteId(cityId, userId, routeId);

        assertThat(result).isEqualTo(expectedMarkers);
    }
}
