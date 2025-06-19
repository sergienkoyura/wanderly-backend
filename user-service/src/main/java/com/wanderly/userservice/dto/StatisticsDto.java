package com.wanderly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatisticsDto {
    private Long totalCompletedARModels;
    private Long totalCompletedRoutes;
    private Long totalCompletedMarkers;
    private List<CityStatisticsDto> cities;
}
