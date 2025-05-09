package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.CityDto;
import com.wanderly.geoservice.entity.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {
    City toCity(CityDto cityDto);

    CityDto toCityDto(City city);
}
