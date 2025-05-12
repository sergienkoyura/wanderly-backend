package com.wanderly.geoservice.mapper.qualifier;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.RouteMarker;
import com.wanderly.geoservice.mapper.MarkerMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RouteQualifier {
    private final MarkerMapper markerMapper;

    @Named("mapMarkers")
    public List<MarkerDto> mapMarkers(List<RouteMarker> routeMarkers) {
        return routeMarkers.stream()
                .map(rm -> {
                    MarkerDto dto = markerMapper.toDto(rm.getMarker());
                    dto.setOrderIndex(rm.getOrderIndex());
                    dto.setStayingTime(rm.getStayingTime());
                    return dto;
                })
                .toList();
    }
}
