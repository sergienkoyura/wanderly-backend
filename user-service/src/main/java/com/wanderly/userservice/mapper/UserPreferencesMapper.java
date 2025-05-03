package com.wanderly.userservice.mapper;

import com.wanderly.userservice.dto.UserPreferencesDto;
import com.wanderly.userservice.entity.UserPreferences;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPreferencesMapper {
    UserPreferences toUserPreferences(UserPreferencesDto userPreferencesDto);
}
