package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.MarkerDto;
import com.wanderly.geoservice.entity.Marker;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    MarkerDto toDto(Marker marker);

    List<MarkerDto> toDtos(List<Marker> markers);
}
