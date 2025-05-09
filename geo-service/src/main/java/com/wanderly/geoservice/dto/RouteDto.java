package com.wanderly.geoservice.dto;

import com.wanderly.geoservice.enums.RouteCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

//todo: validation
@Getter
@Setter
public class RouteDto {
    private UUID id;
    private RouteCategory category;
    private Integer avgTime;
    private Integer avgStayingTime;
    private List<MarkerDto> markers;
}
