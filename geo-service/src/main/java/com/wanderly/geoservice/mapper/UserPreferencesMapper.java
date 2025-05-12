package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.entity.UserPreferences;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CityMapper.class)
public interface UserPreferencesMapper {
    UserPreferencesDto toDto(UserPreferences userPreferences);
}
