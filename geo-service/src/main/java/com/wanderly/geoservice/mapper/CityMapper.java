package com.wanderly.geoservice.mapper;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.geoservice.entity.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {
    City toCity(CityDto cityDto);

    CityDto toCityLookupRequest(City city);
}
