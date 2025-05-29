package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.BranchRequest;
import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.entity.*;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.RouteCategory;
import com.wanderly.geoservice.exception.RouteBranchLimitException;
import com.wanderly.geoservice.exception.RouteNotFoundException;
import com.wanderly.geoservice.exception.RouteRateLimitException;
import com.wanderly.geoservice.exception.RouteSizeLimitException;
import com.wanderly.geoservice.kafka.EraseRouteProgressProducer;
import com.wanderly.geoservice.mapper.RouteMapper;
import com.wanderly.geoservice.repository.RouteRepository;
import com.wanderly.geoservice.service.impl.RouteServiceImpl;
import com.wanderly.geoservice.util.ga.ChromoRoute;
import com.wanderly.geoservice.util.ga.GenMarker;
import com.wanderly.geoservice.util.ga.Router;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock private RouteRepository routeRepository;
    @Mock private CityService cityService;
    @Mock private RouteMapper routeMapper;
    @Mock private UserPreferencesService userPreferencesService;
    @Mock private MarkerService markerService;
    @Mock private EraseRouteProgressProducer eraseRouteProgressProducer;
    @InjectMocks private RouteServiceImpl routeService;

    private UUID cityId;
    private UUID userId;

    @BeforeEach
    void setup() {
        cityId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void generateByCityIdAndUserId_ThrowsRouteRateLimitException() {
        when(markerService.countAllByCityId(cityId)).thenReturn(20);
        when(routeRepository.countAllByCityIdAndUserId(cityId, userId)).thenReturn(1);

        assertThatThrownBy(() -> routeService.generateByCityIdAndUserId(cityId, userId))
                .isInstanceOf(RouteRateLimitException.class);
    }

    @Test
    void generateByCityIdAndUserId_ReturnsRouteDto() {
        Marker marker = new Marker();
        marker.setId(UUID.randomUUID());
        City city = new City();
        city.setId(cityId);
        UserPreferences preferences = UserPreferences.builder().timePerRoute(2).build();
        Route savedRoute = Route.builder().id(UUID.randomUUID()).cityId(cityId).userId(userId).category(RouteCategory.GENERATED).createdAt(LocalDateTime.now()).routeMarkers(new ArrayList<>()).build();

        when(markerService.countAllByCityId(cityId)).thenReturn(100);
        when(routeRepository.countAllByCityIdAndUserId(cityId, userId)).thenReturn(0);
        when(markerService.findAllUnusedByCityIdAndUserId(cityId, userId)).thenReturn(new ArrayList<>(List.of(marker)));
        when(markerService.findAllUnusedByCityId(cityId)).thenReturn(List.of());
        when(markerService.findAllByCityId(cityId)).thenReturn(List.of(marker));
        when(cityService.findById(cityId)).thenReturn(city);
        when(userPreferencesService.findByUserId(userId)).thenReturn(preferences);
        when(routeRepository.save(any())).thenReturn(savedRoute);
        when(routeMapper.toDto(any())).thenReturn(new RouteDto());

        try (MockedStatic<Router> mocked = mockStatic(Router.class)) {
            GenMarker gen = new GenMarker(marker.getId(), 1.0, -1.0, 20, 10, MarkerCategory.NATURE);
            ChromoRoute chromo = new ChromoRoute(List.of(gen), 100, 10);
            mocked.when(() -> Router.generateRoute(eq(city), anyList(), eq(preferences), isNull())).thenReturn(chromo);
            mocked.when(() -> Router.calculateTime(any(), anyInt(), any())).thenReturn(10);

            RouteDto result = routeService.generateByCityIdAndUserId(cityId, userId);
            assertThat(result).isNotNull();
        }
    }

    @Test
    void save_ThrowsRouteNotFoundException_WhenRouteNotExists() {
        RouteDto dto = new RouteDto();
        dto.setId(UUID.randomUUID());
        when(routeRepository.findByIdAndUserId(dto.getId(), userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.save(userId, dto))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void save_ThrowsRouteSizeLimitException_WhenNotEnoughMarkers() {
        RouteDto dto = new RouteDto();
        dto.setId(UUID.randomUUID());
        dto.setMarkers(List.of(new MarkerDto()));

        when(routeRepository.findByIdAndUserId(dto.getId(), userId)).thenReturn(Optional.of(Route.builder().routeMarkers(new ArrayList<>()).build()));

        assertThatThrownBy(() -> routeService.save(userId, dto))
                .isInstanceOf(RouteSizeLimitException.class);
    }

    @Test
    void branch_ThrowsRouteBranchLimitException_WhenMarkerIndexTooHigh() {
        UUID routeId = UUID.randomUUID();
        BranchRequest request = new BranchRequest(routeId, 2);

        Route route = Route.builder()
                .id(routeId)
                .cityId(cityId)
                .userId(userId)
                .routeMarkers(List.of(new RouteMarker(), new RouteMarker()))
                .build();

        when(routeRepository.findByIdAndUserId(routeId, userId)).thenReturn(Optional.of(route));

        assertThatThrownBy(() -> routeService.branch(userId, request))
                .isInstanceOf(RouteBranchLimitException.class);
    }
}
