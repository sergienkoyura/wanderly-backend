package com.wanderly.geoservice.service.impl;

import com.wanderly.geoservice.entity.*;
import com.wanderly.geoservice.enums.ActivityType;
import com.wanderly.geoservice.enums.TravelType;
import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import com.wanderly.geoservice.enums.RouteCategory;
import com.wanderly.geoservice.exception.StartingPointNotFoundException;
import com.wanderly.geoservice.mapper.RouteMapper;
import com.wanderly.geoservice.repository.MarkerRepository;
import com.wanderly.geoservice.repository.RouteRepository;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.RouteService;
import com.wanderly.geoservice.service.UserPreferencesService;
import com.wanderly.geoservice.util.ga.ChromoRoute;
import com.wanderly.geoservice.util.ga.GenMarker;
import com.wanderly.geoservice.util.ga.Router;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final CityService cityService;
    private final RouteMapper routeMapper;
    private final MarkerRepository markerRepository;
    private final UserPreferencesService userPreferencesService;

    @Override
    public List<RouteDto> findAllByCityIdAndUserId(UUID cityId, UUID userId) {
//        List<Route> existingRoutes = routeRepository.findAllByCityId(cityId);
//        if (!existingRoutes.isEmpty()) {
//            return routeMapper.toDtos(existingRoutes);
//        }

        return List.of(generateByCityIdAndUserId(cityId, userId));

//        List<Marker> markers = markerRepository.findAllByCityId(cityId);
//
//        List<Route> generatedRoutes = generateRoutes(cityId, markers, userId);
//
//        return routeMapper.toDtos(routeRepository.saveAll(generatedRoutes));
    }

    private List<Route> generateRoutes(UUID cityId, List<Marker> markers, UUID userId) {
        // 1. Shuffle and chunk markers to groups of 5
        // 2. Create Route + RouteMarker with proper order
        // 3. Calculate avg_time from marker.avg_time
        // 4. Return List<Route>
        List<Route> result = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Collections.shuffle(markers);
            Route route = new Route();
//            route.setId(UUID.randomUUID());
            route.setCityId(cityId);
            route.setUserId(userId);
            route.setCategory(RouteCategory.GENERATED);
            route.setRouteMarkers(new ArrayList<>());

            int sumTime = 0;
            for (int j = 0; j < 5; j++) {
                Marker m = markers.get(j);
                RouteMarker rm = new RouteMarker();
                rm.setRoute(route);
                rm.setMarker(m);
                rm.setOrderIndex(i);
                route.getRouteMarkers().add(rm);

//                sumTime += m.getAvgTime();
            }
            route.setAvgTime(sumTime);
            result.add(route);
        }

        return result;
    }

    @Override
    public RouteDto generateByCityIdAndUserId(UUID cityId, UUID userId) {

        // Phase 1: Filtering
        // markers that are used by other users but unused by current
        List<Marker> markersUnusedByUser = markerRepository.findAllUnusedByCityIdAndUserId(cityId, userId);

        // markers that are unused at all
        List<Marker> markersUnused = markerRepository.findAllUnusedByCityId(cityId);

        List<Marker> markers = new ArrayList<>();
        markers.addAll(markersUnusedByUser);
        markers.addAll(markersUnused);

        // to make the route valid, if less - there are bad markers todo to think about it
        if (markers.size() < 30) {
            markers = markerRepository.findAllByCityId(cityId);
        }


        // Phase 2: Start point
        City city = cityService.findById(cityId);
        // user coordinates: 49.817440, 24.061298

        UserPreferences userPreferences = userPreferencesService.findByUserId(userId);

        ChromoRoute chromoRoute = Router.generateRoute(city, markers, userPreferences);
        List<Marker> chosenMarkers = markers.stream()
                .filter(m -> chromoRoute.getMarkers().stream()
                        .map(GenMarker::getId).toList()
                        .contains(m.getId())).toList();

        Route route = Route.builder()
                .cityId(cityId)
                .userId(userId)
                .avgTime(chromoRoute.getTotalDuration())
                .category(RouteCategory.GENERATED)
                .build();

//        Route savedRoute = routeRepository.save(route);

        List<RouteMarker> routeMarkers = new ArrayList<>();
        for (int i = 0; i < chosenMarkers.size(); i++) {
            int finalI = i;
            routeMarkers.add(RouteMarker.builder()
                    .orderIndex(i + 1)
                    .stayingTime(chromoRoute.getMarkers().stream()
                            .filter(el -> el.getId().equals(chosenMarkers.get(finalI).getId()))
                            .findFirst().get().getStayingTime())
                    .marker(chosenMarkers.get(i))
                    .route(route)
                    .build());
        }
        route.setRouteMarkers(routeMarkers);
        route.setId(UUID.randomUUID());
        return routeMapper.toDto(route);

//        savedRoute.setRouteMarkers(routeMarkers);
//        savedRoute = routeRepository.save(savedRoute);
//
//        return routeMapper.toDto(savedRoute);
    }


    private Route generateRoute(UUID cityId, UUID userId) {

//        UserPreferencesDetailsDto prefs = userPreferencesReplyingProducer.requestUPDetails(userId);
//
//        Marker start = selectStartPoint(prefs, city, markers, 0);
//        System.out.println(start);

        return new Route();
    }


    public int getRelevanceScore(UserPreferences userPrefs, Marker marker) {
        int score = 0;

        // 1. Оцінка категорії маркера
        score += getCategoryScore(userPrefs.getActivityType(), marker.getCategory());

        // 2. Оцінка тега маркера
        score += getTagScore(marker.getTag());

        // 3. TravelType - пріоритети: Scenic для FOOT, Entertainment/Food для CAR
        score += getTravelTypeBonus(userPrefs.getTravelType(), marker.getCategory());

        return score;
    }

    private int getCategoryScore(ActivityType activityType, MarkerCategory category) {
        switch (activityType) {
            case INDOOR -> {
                return switch (category) {
                    case LANDMARK -> 8;
                    case ENTERTAINMENT -> 6;
                    case FOOD -> 5;
                    default -> 0;
                };
            }
            case OUTDOOR -> {
                return switch (category) {
                    case NATURE -> 8;
                    case SCENIC -> 7;
                    case FOOD -> 5;
                    default -> 0;
                };
            }
            case COMBINED -> {
                return switch (category) {
                    case LANDMARK, NATURE -> 6;
                    case ENTERTAINMENT, SCENIC -> 5;
                    case FOOD -> 4;
                    default -> 0;
                };
            }
        }
        return 0;
    }

    private int getTagScore(MarkerTag tag) {
        return switch (tag) {
            // TOP LANDMARKS
            case CASTLE, MONUMENT, MUSEUM, GALLERY -> 5;
            case VIEWPOINT, TRAILHEAD -> 4;
            case THEME_PARK, ATTRACTION, THEATRE -> 3;
            case CAFE, RESTAURANT -> 2;
            default -> 1;
        };
    }

    private int getTravelTypeBonus(TravelType travelType, MarkerCategory category) {
        if (travelType == TravelType.FOOT) {
            return switch (category) {
                case SCENIC, LANDMARK -> 2;
                case FOOD -> 1;
                default -> 0;
            };
        } else if (travelType == TravelType.CAR) {
            return switch (category) {
                case ENTERTAINMENT, FOOD -> 2;
                default -> 0;
            };
        }
        return 0;
    }

    private static final double DEFAULT_RADIUS_KM = 3.0;

    public Marker selectStartPoint(UserPreferences userPrefs,
                                   City city,
                                   List<Marker> markers,
                                   int userGeneratedRoutes) {
        // Центр міста
        double centerLat = city.getLatitude();
        double centerLon = city.getLongitude();

        // Визначити радіус
        double radiusKm = userGeneratedRoutes < 2 ? DEFAULT_RADIUS_KM : Double.MAX_VALUE;

        return markers.stream()
                .filter(marker -> haversineDistance(centerLat, centerLon, marker.getLatitude(), marker.getLongitude()) <= radiusKm)
                .max(Comparator.comparingInt(marker -> getRelevanceScore(userPrefs, marker)))
                .orElseThrow(StartingPointNotFoundException::new); // Якщо немає кандидатів — null або обробити як виняток
    }

    // Haversine формула (в км)
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // км
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

}
