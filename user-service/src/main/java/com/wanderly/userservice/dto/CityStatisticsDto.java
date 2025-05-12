package com.wanderly.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CityStatisticsDto {
    private String name;
    private Long inProgressRoutes;
    private Long completedRoutes;
    private Long completedARModels;
}
