package com.wanderly.geoservice.service.impl;

import com.wanderly.common.dto.EraseRouteProgressMessage;
import com.wanderly.geoservice.dto.BranchRequest;
import com.wanderly.geoservice.entity.*;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.enums.RouteCategory;
import com.wanderly.geoservice.exception.RouteSizeLimitException;
import com.wanderly.geoservice.exception.RouteNotFoundException;
import com.wanderly.geoservice.exception.RouteRateLimitException;
import com.wanderly.geoservice.exception.RouteBranchLimitException;
import com.wanderly.geoservice.kafka.EraseRouteProgressProducer;
import com.wanderly.geoservice.mapper.RouteMapper;
import com.wanderly.geoservice.repository.RouteRepository;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.MarkerService;
import com.wanderly.geoservice.service.RouteService;
import com.wanderly.geoservice.service.UserPreferencesService;
import com.wanderly.geoservice.util.ga.ChromoRoute;
import com.wanderly.geoservice.util.ga.Router;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final CityService cityService;
    private final RouteMapper routeMapper;
    private final UserPreferencesService userPreferencesService;
    private final MarkerService markerService;
    private final EraseRouteProgressProducer eraseRouteProgressProducer;

    @Override
    public List<RouteDto> findAllByCityIdAndUserId(UUID cityId, UUID userId) {
        List<Route> existingRoutes = routeRepository.findAllByCityIdAndUserId(cityId, userId);
        if (!existingRoutes.isEmpty()) {
            return routeMapper.toDtos(existingRoutes);
        }

        return new ArrayList<>();
    }

    @Override
    public RouteDto generateByCityIdAndUserId(UUID cityId, UUID userId) {

        // Rate limitation: to make the output more valid
        int markersCount = markerService.countAllByCityId(cityId);
        int routesCount = routeRepository.countAllByCityIdAndUserId(cityId, userId);
        if (markersCount < 30 && routesCount == 1 ||
            markersCount < 50 && routesCount == 2 ||
            markersCount < 80 && routesCount == 3 ||
            markersCount < 100 && routesCount == 4 ||
            routesCount >= 5) {
            throw new RouteRateLimitException();
        }

        // markers that are used by other users but unused by current
        List<Marker> markersUnusedByUser = markerService.findAllUnusedByCityIdAndUserId(cityId, userId);

        return routeBuilder(markersUnusedByUser, null, cityId, userId, null);
    }

    @Override
    @Transactional
    public void deleteByRouteIdAndUserId(UUID routeId, UUID userId) {
        // only the route owner can delete a route
        routeRepository.deleteByIdAndUserId(routeId, userId);
    }

    @Override
    public RouteDto save(UUID userId, RouteDto routeDto) {
        Route route = routeRepository.findByIdAndUserId(routeDto.getId(), userId)
                .orElseThrow(RouteNotFoundException::new);

        // delete existing progress by this route
        eraseRouteProgressProducer.sendProgressEraseMessage(new EraseRouteProgressMessage(routeDto.getId()));

        UserPreferences userPreferences = userPreferencesService.findByUserId(userId);

        List<Marker> newMarkers = routeDto.getMarkers().stream()
                .map(el -> markerService.findById(el.getId()))
                .toList();

        if (newMarkers.size() < 2) {
            throw new RouteSizeLimitException();
        }

        List<RouteMarker> convertedMarkers = convertMarkers(newMarkers, route, userPreferences);

        route.getRouteMarkers().clear();
        route.getRouteMarkers().addAll(convertedMarkers);
        Route savedRoute = routeRepository.save(route);

        return routeMapper.toDto(savedRoute);
    }

    @Override
    public RouteDto branch(UUID userId, BranchRequest branchRequest) {
        Route route = routeRepository.findByIdAndUserId(branchRequest.routeId(), userId)
                .orElseThrow(RouteNotFoundException::new);

        // delete existing progress by this route
        eraseRouteProgressProducer.sendProgressEraseMessage(new EraseRouteProgressMessage(route.getId()));

        List<RouteMarker> routeMarkers = route.getRouteMarkers();

        if (branchRequest.markerIndex() + 1 >= routeMarkers.size()) {
            throw new RouteBranchLimitException();
        }

        UUID cityId = route.getCityId();

        List<RouteMarker> prefix = routeMarkers.subList(0, branchRequest.markerIndex() + 1);


        // markers that are used by other users but unused by current OR used in this routeId
        List<Marker> markersUnusedByUserExceptRoute = markerService.findAllUnusedByCityIdAndUserIdExceptRouteId(cityId, userId, route.getId());

        return routeBuilder(markersUnusedByUserExceptRoute, route.getId(), cityId, userId, prefix);
    }

    private RouteDto routeBuilder(List<Marker> markers,
                                  UUID routeId,
                                  UUID cityId,
                                  UUID userId,
                                  List<RouteMarker> prefix) {
        // markers that are unused at all
        List<Marker> markersUnused = markerService.findAllUnusedByCityId(cityId);
        markers.addAll(markersUnused);

        // to make the route valid, if less - there are bad markers
        if (markers.size() < 30) {
            markers = markerService.findAllByCityId(cityId);
        }

        City city = cityService.findById(cityId);

        UserPreferences userPreferences = userPreferencesService.findByUserId(userId);

        ChromoRoute chromoRoute = Router.generateRoute(city, markers, userPreferences, prefix);
        Map<UUID, Marker> markerMap = markers.stream()
                .collect(Collectors.toMap(Marker::getId, Function.identity()));

        List<Marker> chosenMarkers = chromoRoute.getMarkers().stream()
                .map(gen -> markerMap.get(gen.getId()))
                .filter(Objects::nonNull)
                .toList();

        Route route = Route.builder()
                .id(routeId)
                .cityId(cityId)
                .userId(userId)
                .avgTime(chromoRoute.getTotalDuration())
                .category(RouteCategory.GENERATED)
                .createdAt(LocalDateTime.now())
                .build();

        Route savedRoute = routeRepository.save(route);

        List<RouteMarker> convertedMarkers = convertMarkers(chosenMarkers, savedRoute, userPreferences);

        if (savedRoute.getRouteMarkers() == null) {
            savedRoute.setRouteMarkers(convertedMarkers);
        } else {
            savedRoute.getRouteMarkers().clear();
            savedRoute.getRouteMarkers().addAll(convertedMarkers);
        }

        savedRoute = routeRepository.save(savedRoute);
        return routeMapper.toDto(savedRoute);
    }

    private List<RouteMarker> convertMarkers(List<Marker> markers, Route route, UserPreferences userPreferences) {
        List<RouteMarker> routeMarkers = new ArrayList<>();
        for (int i = 0; i < markers.size(); i++) {
            Marker currentMarker = markers.get(i);

            routeMarkers.add(RouteMarker.builder()
                    .orderIndex(i + 1)
                    .stayingTime(Router.calculateTime(
                            currentMarker.getTag(),
                            userPreferences.getTimePerRoute(),
                            userPreferences.getTravelType()
                    ))
                    .marker(currentMarker)
                    .route(route)
                    .build());
        }

        return routeMarkers;
    }

}
