package com.wanderly.userservice.service.impl;

import com.wanderly.userservice.dto.CityStatisticsDto;
import com.wanderly.userservice.dto.StatisticsDto;
import com.wanderly.userservice.repository.ARModelCompletionRepository;
import com.wanderly.userservice.repository.UserRouteCompletionRepository;
import com.wanderly.userservice.service.StatisticsService;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final ARModelCompletionRepository arModelCompletionRepository;
    private final UserRouteCompletionRepository userRouteCompletionRepository;

    @Override
    public StatisticsDto getStatistics(UUID userId) {
        StatisticsDto statisticsDto = new StatisticsDto();

        long totalCompletedARModels = arModelCompletionRepository.countCompletedARModelsByUserId(userId);
        long totalCompletedRoutes = userRouteCompletionRepository.countCompletedRoutesByUserId(userId);
        long totalCompletedMarkers = userRouteCompletionRepository.countCompletedMarkersByUserId(userId);

        List<CityStatisticsDto> cities = new ArrayList<>();
        List<Tuple> citiesRoutes = userRouteCompletionRepository.getRouteStatsPerCity(userId);
        List<Tuple> citiesARModels = arModelCompletionRepository.getARModelStatsPerCity(userId);

        for (Tuple cityRoute : citiesRoutes) {
            String cityName = cityRoute.get("cityName", String.class);
            long completedRoutes = cityRoute.get("completedRoutes", Long.class);
            long inProgressRoutes = cityRoute.get("inProgressRoutes", Long.class);

            cities.add(CityStatisticsDto.builder()
                    .name(cityName)
                    .completedRoutes(completedRoutes)
                    .inProgressRoutes(inProgressRoutes)
                    .completedARModels(0L)
                    .build());
        }

        for (Tuple cityModel : citiesARModels) {
            String cityName = cityModel.get("cityName", String.class);
            long completedARModels = cityModel.get("completedARModels", Long.class);

            Optional<CityStatisticsDto> dto = cities.stream().filter(el -> el.getName().equals(cityName)).findAny();
            if (dto.isPresent()) {
                dto.get().setCompletedARModels(completedARModels);
            } else {
                cities.add(CityStatisticsDto.builder()
                        .name(cityName)
                        .completedRoutes(0L)
                        .inProgressRoutes(0L)
                        .completedARModels(completedARModels)
                        .build());
            }
        }

        cities.sort(Comparator
                .comparingInt(el -> Math.toIntExact(((CityStatisticsDto) el).getCompletedRoutes() + ((CityStatisticsDto) el).getCompletedARModels()))
                .reversed()
        );

        statisticsDto.setTotalCompletedARModels(totalCompletedARModels);
        statisticsDto.setTotalCompletedRoutes(totalCompletedRoutes);
        statisticsDto.setTotalCompletedMarkers(totalCompletedMarkers);
        statisticsDto.setCities(cities);

        return statisticsDto;
    }
}
