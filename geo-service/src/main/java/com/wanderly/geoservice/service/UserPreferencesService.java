package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.entity.UserPreferences;

import java.util.UUID;

public interface UserPreferencesService {
    UserPreferencesDto findDtoByUserId(UUID userId);

    UserPreferencesDto save(UUID userId, UserPreferencesDto userPreferencesDto);

    UserPreferences findByUserId(UUID userId);
}
