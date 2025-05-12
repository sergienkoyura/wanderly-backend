package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.RouteDto;
import com.wanderly.geoservice.entity.Route;
import com.wanderly.geoservice.mapper.qualifier.RouteQualifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = RouteQualifier.class)
public interface RouteMapper {

    @Mapping(source = "routeMarkers", target = "markers", qualifiedByName = "mapMarkers")
    RouteDto toDto(Route route);

    List<RouteDto> toDtos(List<Route> routes);
}
