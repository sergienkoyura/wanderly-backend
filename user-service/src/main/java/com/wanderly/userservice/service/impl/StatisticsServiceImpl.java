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

        List<CityStatisticsDto> cities = new ArrayList<>();
        List<Tuple> citiesRoutes = userRouteCompletionRepository.getRouteStatsPerCity(userId);
        List<Tuple> citiesARModels = arModelCompletionRepository.getARModelStatsPerCity(userId);

        Map<String, Long> arModelsByCity = citiesARModels.stream()
                .collect(Collectors.toMap(
                        t -> t.get("cityName", String.class),
                        t -> (t.get("completedARModels", Long.class))
                ));

        for (Tuple cityRoute : citiesRoutes) {
            String cityName = cityRoute.get("cityName", String.class);
            long completedRoutes = cityRoute.get("completedRoutes", Long.class);
            long inProgressRoutes = cityRoute.get("inProgressRoutes", Long.class);
            long completedARModels = arModelsByCity.getOrDefault(cityName, 0L);

            cities.add(CityStatisticsDto.builder()
                    .name(cityName)
                    .completedRoutes(completedRoutes)
                    .inProgressRoutes(inProgressRoutes)
                    .completedARModels(completedARModels)
                    .build());
        }

        cities.sort(Comparator
                .comparingInt(el -> Math.toIntExact(((CityStatisticsDto) el).getCompletedRoutes() + ((CityStatisticsDto) el).getCompletedARModels()))
                .reversed()
        );

        statisticsDto.setTotalCompletedARModels(totalCompletedARModels);
        statisticsDto.setTotalCompletedRoutes(totalCompletedRoutes);
        statisticsDto.setCities(cities);

        return statisticsDto;
    }
}
